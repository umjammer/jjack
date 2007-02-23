package de.gulden.framework.jjack.util.benchmark;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.util.Calendar;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import de.gulden.framework.jjack.JJackSystem;
import de.gulden.framework.jjack.util.JJackPlayer;
import de.gulden.framework.jjack.util.JJackRecorder;

/**
 * Remote-benchmarkig of audio latency. Two machines, connected via network and via analog audio.
 * 
 * Machine 1 (tester): sends a udp-package as start signal and records incoming audio via JJack.
 * Machine 2 (tested): waits for udp-package, the outputs a one-pulse test signal, either via JJack or JavaSound.
 * 
 * @author Jens Gulden
 */
public class AudioBenchmark {
	
	final static boolean ENDIANESS = false; // little endian

	int mode;
	InetAddress ip;
	DatagramSocket socket;
	boolean test = false;
	
	/**
	 * Usage:
	 *  
	 * AudioBenchmark jjack | javasound [buffer-size] | <IP-adress> <configuration-info> [<test-count>]
	 * 
	 * If an ip-address is specified, the program acts as the tester for the specified remote machine.
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		(new AudioBenchmark()).go(args);
	}
		
	public void go(String[] args) throws Exception {
		
		//System.setProperty("jjack.ports.in", "0");
		//System.setProperty("jjack.ports.out", "2");
		//System.setProperty("jjack.ports.autoconnect", "true");
		//System.setProperty("jjack.ports.out.autoconnect", "true");
		
		String modeStr = args[0];
		if (modeStr.equalsIgnoreCase("jjack")) {
			mode = 2;
		} else if (modeStr.equalsIgnoreCase("javasound")) {
			mode = 1;
		} else {
			mode = 0;
			ip = InetAddress.getByName(modeStr);
		}
		
		if (args[args.length-1].equalsIgnoreCase("test")) {
			test = true; // will skip udp ping/wait
		}
		
		if (mode == 0) { // ### tester #######################################
			
			String configurationInfo = args[1];
			int testCount = 1;
			if (args.length >= 3) {
				try {
					testCount = Integer.parseInt(args[2]);
				} catch (NumberFormatException nfe) {
					// nop
				}
			}
			
			System.setProperty("jjack.ports.in", "2");
			System.setProperty("jjack.ports.out", "0");
			System.setProperty("jjack.client.name", "Tester");
			System.setProperty("jjack.ports.in.autoconnect", "true");
			
			//String filename = findFilename();
			Calendar c = Calendar.getInstance();
			String ts = "" + c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" + c.get(Calendar.DAY_OF_MONTH) +"" + c.get(Calendar.HOUR) + "" + c.get(Calendar.MINUTE) + "" + c.get(Calendar.SECOND);     
			String filename = "test-" + ts +"-";
			
			System.out.println("--- Analysis " + filename + "* ---");
			StringBuffer protocol = new StringBuffer();
			protocol.append(filename);
			protocol.append("*: ");
			protocol.append(configurationInfo);
			protocol.append("\n\n");
			
			// start recording
			int sampleRate = JJackSystem.getSampleRate(); // also initializes JJackSystem

			socket = new DatagramSocket(); 
			
			for (int testNr = 1; testNr <= testCount; testNr++) {
				
				String testNrStr = String.valueOf(testNr);
				while (testNrStr.length() < 3) testNrStr = '0' +testNrStr;
				
				System.out.print("Test "+testNrStr+": ");
				
				float[][] data = new float[2][sampleRate * 1]; // 1 sec
				JJackRecorder recorder = new JJackRecorder(data);
				JJackSystem.setProcessor(recorder);
				
				Thread.sleep(100); // wait a bit until everything runs smooth
				
				// send udp packet as start signal
				if (!test) {
					SocketAddress target = new InetSocketAddress(ip, 7777);
					DatagramPacket packet = new DatagramPacket(new byte[] { 'g', 'o' }, 2, target);
					socket.send(packet);
				}
				
				int startPos = recorder.getPosition();
				
				// wait 1 second for recording to finish
				//Thread.sleep(1000);
				//recorder.stop();
				while ( recorder.isRecording() ) { // recorder will stop when data is full
					Thread.sleep(100);
				}
				
				// truncate beginning if there had been recorded something before we ourselves have sent the udp-ping
				//System.out.println("truncating initial " + startPos + " frames (= "+((startPos*1000)/sampleRate) +" ms) recorded before udp-ping was sent");
				data = JJackRecorder.cut(data, startPos, data[0].length);
				
				
				// write as .pcm file
				short[][] pcm = JJackRecorder.floatToPCM(data);
				short[] p = JJackRecorder.interleavePCM(pcm);
				byte[] b = JJackRecorder.encodeBytes(p, true, ENDIANESS);
				/*DataOutputStream f = new DataOutputStream(new FileOutputStream(filename));
				for (int i = 0; i < p.length; i++) {
					f.writeShort(p[i]);
				}*/
				OutputStream f = new FileOutputStream(filename + testNrStr + ".pcm");
				f.write(b);
				f.close();
				
				// write as .wav file
				File file = new File(filename + testNrStr + ".wav");
				AudioFormat af = new AudioFormat(44100, 16, 2, true, ENDIANESS);
				AudioInputStream ais = new AudioInputStream(new ByteArrayInputStream(b), af, pcm[0].length);
				AudioSystem.write(ais, AudioFileFormat.Type.WAVE, file);

				// do analysis
				int framePos = analyze(data, 0.2f, 10);
				float ms;
				if (framePos != -1) {
					ms = ((float)framePos * 1000) / sampleRate;
					//System.out.println("sequence of 10 signal values above 0.2 detected at frame "+framePos+": latency "+ms+" ms");
					System.out.println(ms+" ms");
				} else {
					//System.out.println("no significant signal level detected");
					System.out.println("no signal detected");
					ms = -1;
				}
				float startPosMs = ((float)startPos * 1000) / sampleRate;
				protocol.append( ms ).append('\t').append(framePos).append('\t').append( startPosMs ).append('\t').append( startPos ).append('\n');

			}
			
			// write analysis protocol as text file
			PrintWriter pw = new PrintWriter(new FileWriter(filename + "analysis.txt"));
			pw.print(protocol);
			pw.close();
			
			System.exit(0);
			
			
		} else { // ### player ###############################################
			
			if (!test) {
				socket = new DatagramSocket(7777);
			}

			if (mode == 2) { // JJack
				
				System.setProperty("jjack.ports.in", "0");
				System.setProperty("jjack.ports.out", "2");
				System.setProperty("jjack.ports.out.autoconnect", "true");
				
				class SquarePlayer extends JJackPlayer {
					boolean on = false;
					public float getMonoSampleAt(int i) {
						return on ? ((((i/50)%2) == 0) ? 1.0f : -1.0f) : 0f; // minimum latency: able to react while inside process() 
					}
				};			
				
				SquarePlayer player = new SquarePlayer();
				
				JJackSystem.setProcessor(player);
				
				while (true) {
					
					waitForUDP();
					
					player.on = true;
					
					Thread.sleep(100);
					
					player.on = false;
					
					System.out.println("sound played");
					
					Thread.sleep(100); 
					
				}
				
				//System.exit(0);
				// exit via ctrl-c
				
				
			} else { // JavaSound
				
				int javasoundBufferSize = -1;
				if (args.length >= 2) {
					String bufSize = args[1];
					try {
						javasoundBufferSize =Integer.parseInt(bufSize);
					} catch (NumberFormatException nfe) {
						// nop
					}
				}
				
				AudioFormat format = new AudioFormat(44100, 16, 2, true, true); 
				DataLine.Info infoOut = new DataLine.Info(SourceDataLine.class, format);
				Mixer.Info m = AudioSystem.getMixerInfo()[0];
				SourceDataLine lineOut = (SourceDataLine)AudioSystem.getMixer(m).getLine(infoOut);
				if (javasoundBufferSize != -1) {
		            lineOut.open(format,javasoundBufferSize);
				} else {
		            lineOut.open(format);
				}
		        lineOut.start();
		        System.out.println("Buffersize: "+javasoundBufferSize+" / "+lineOut.getBufferSize());
		        
		        byte[] b = square( 44100 * 3 / 8, 50*2*2 ); // ~100ms (worked-around), should be 44100/10, 100
		        
		        while (true) {
		        	
			        waitForUDP();
			        
			        lineOut.write(b, 0, b.length);
			        
			        Thread.sleep(100);
			        System.out.println("sound played");
			        
			        //Thread.sleep(100);
		        }
				
		        // exit via ctrl-c
			}
		}			
	}
	
	private void waitForUDP() throws IOException {
		if (test) return;
		// wait for udp start package
		DatagramPacket packet = new DatagramPacket(new byte[2], 2);
		
		System.out.println("waiting for UDP packet");
		socket.receive(packet);
		
		byte[] b = packet.getData();
		if ( ! (b[0]=='g' && b[1]=='o') ) {
			throw new IOException("illegal UDP packet received (JJack benchmark)");
		}		
	}
	
	private static int analyze(float[][] data, float signalThreshold, int timeThreshold) {
		StringBuffer report = new StringBuffer();
		
		float[] mono = JJackRecorder.downmixMono(data);
		
		int SEARCH_COUNT = 10;
		float SEARCH_THRESHOLD = 0.2f;
		
		// search for first occurrence of SEARCH_COUNT samples above 0.2 in a row
		int count = 0;
		int pos = 0;
		while ((pos < mono.length) && (count != timeThreshold)) {
			if ( mono[pos++] > signalThreshold ) {
				count++;
			} else {
				count = 0;
			}
		}
		
		if (count == SEARCH_COUNT) {
			pos -= SEARCH_COUNT;
		} else {
			pos = -1;
		}
		
		return pos;
	}

	/*private static String analyze(float[][] data) {
		StringBuffer report = new StringBuffer();
		
		float[] mono = JJackRecorder.downmixMono(data);
		
		int SEARCH_COUNT = 10;
		float SEARCH_THRESHOLD = 0.2f;
		
		// search for first occurrence of SEARCH_COUNT samples above 0.2 in a row
		int count = 0;
		int pos = 0;
		while ((pos < mono.length) && (count != SEARCH_COUNT)) {
			if ( mono[pos++] > SEARCH_THRESHOLD ) {
				count++;
			} else {
				count = 0;
			}
		}
		
		if (count == SEARCH_COUNT) {
			pos -= SEARCH_COUNT;
			report.append("Signal of "+SEARCH_COUNT+" samples above "+SEARCH_THRESHOLD+" detected at sample " + pos);
			int ms = (pos * 1000) / JJackSystem.getSampleRate();
			report.append("= latency time " + ms + " ms.");
		} else {
			report.append("no valid signal received");
		}
		
		return report.toString();
	}*/

	private static String findFilename() {
		int i = 1;
		do {
			String filename = "test" + leadingzeros(i, 3) + ".pcm";
			File file = new File(filename);
			if ( ! file.exists() ) {
				return filename;
			}
			i++;
		} while (true);
	}
	
	private static String leadingzeros(int i, int total) {
		String s = String.valueOf(i);
		while (s.length() < total) {
			s = "0" + s;
		}
		return s;
	}
	
	private static byte[] square(int samples, int phase) { // something's wrong with this, but works as calling parameters work around this  
		byte[] b = new byte[samples * 2 * 2];
		//int k = 0;
		for (int i = 0; i < samples; i+= phase) {
			short val;
			if (((i / phase) % 2) == 0 ) {
				val = Short.MAX_VALUE;
			} else {
				val = Short.MIN_VALUE;
			}
			byte b1 = (byte)((val >> 8) & 0xff);
			byte b2 = (byte)(val & 0xff);
			int k = i;
			for (int j = 0; j < phase; j++) {
				if (ENDIANESS) {
					b[(k++)] = b2; // left
					b[(k++)] = b1;
					b[(k++)] = b2; // right
					b[(k++)] = b1;
				} else {
					b[(k++)] = b1; // left
					b[(k++)] = b2;
					b[(k++)] = b1; // right
					b[(k++)] = b2;
				}
			}
		}
		return b;
	}
}
