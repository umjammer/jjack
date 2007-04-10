package com.petersalomonsen.jjack.javasound;

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixer
 * Version: 0.3
 *
 * Date:    2007-04-09
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Control.Type;

import de.gulden.framework.jjack.JJackAudioEvent;
import de.gulden.framework.jjack.JJackClient;
import de.gulden.framework.jjack.JJackSystem;

/**
 * A Javasound Mixer implementation that enables use of jack through this standard java interface..
 * 
 * @author Peter Johan Salomonsen
 */
public class JJackMixer implements Mixer {

	class JJackLine extends JJackClient implements SourceDataLine
	{	
		AudioFormat format;
		
		ByteBuffer byteBuffer;
		
		ShortBuffer shortBuffer;
		
		byte[] buffer;
		
		long bufferPosWrite = 0;
		long bufferPosRead = 0;
		long longFramePosition = 0;

		boolean running = false;
		boolean open = false;
		
		public JJackLine()
		{
		}

		@Override
		public void process(JJackAudioEvent e) {
			if(bufferPosWrite-bufferPosRead >= e.getOutput().capacity() * e.getOutputs().length * 2)
			{
				for(int n=0;n<e.getOutput().capacity()*e.getOutputs().length;n++)
				{
					e.getOutputs()[n%e.getOutputs().length].put(n/e.getOutputs().length, shortBuffer.get( (int)(bufferPosRead%buffer.length)/2  ) / 32768f);				
					bufferPosRead+=2;
				}
				releaseBlock();
				longFramePosition = bufferPosRead / (e.getOutputs().length * 2);
			}	
		}

		public void open(AudioFormat format) throws LineUnavailableException {
			open(format,65536);
		}

		public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
			this.format = format;
			byteBuffer = ByteBuffer.allocate(bufferSize);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			shortBuffer = byteBuffer.asShortBuffer();
			buffer = byteBuffer.array();
			
			try
			{
				JJackSystem.setProcessor(this);
			} catch(Exception e)
			{
				throw new LineUnavailableException();
			}
			
			open = true;
		}

		private synchronized void releaseBlock()
		{
			notify();	
		}
		
		private synchronized void block()
		{
//			long blockStart = System.currentTimeMillis();
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			System.out.println(System.currentTimeMillis()-blockStart);
		}
		
		public int write(byte[] b, int off, int len) {
		
			while(bufferPosWrite+len-bufferPosRead > buffer.length)
				block();
		
			int localBufferPos = (int)(bufferPosWrite%buffer.length);
			if(localBufferPos+len > buffer.length)
			{	
				System.arraycopy(b, off, buffer, localBufferPos, buffer.length-localBufferPos);	
				System.arraycopy(b, off, buffer, 0, len - (buffer.length-localBufferPos));
			}
			else
				System.arraycopy(b, off, buffer, localBufferPos,len);
			
			bufferPosWrite+=len;	
			return len;
		}

		public int available() {
			return bufferPosWrite-bufferPosRead > buffer.length ? buffer.length : (int)(bufferPosWrite-bufferPosRead);
		}

		public void drain() {
			// TODO Auto-generated method stub
			
		}

		public void flush() {
			bufferPosRead = bufferPosWrite;
		}

		public int getBufferSize() {
			return buffer.length;
		}

		public AudioFormat getFormat() {
			return format;
		}

		public int getFramePosition() {
			return (int)(longFramePosition % Math.pow(2,31));
		}

		public float getLevel() {
			// TODO Auto-generated method stub
			return 0;
		}

		public long getLongFramePosition() {
			return longFramePosition;
		}

		public long getMicrosecondPosition() {
			// TODO Auto-generated method stub
			return 0;
		}

		public boolean isActive() {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isRunning() {
			return running;
		}

		public void start() {
			running = true;
		}

		public void stop() {
			running = false;
		}

		public void addLineListener(LineListener listener) {
			// TODO Auto-generated method stub
			
		}

		public void close() {
			open = false;
		}

		public Control getControl(Type control) {
			// TODO Auto-generated method stub
			return null;
		}

		public Control[] getControls() {
			// TODO Auto-generated method stub
			return null;
		}

		public javax.sound.sampled.Line.Info getLineInfo() {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean isControlSupported(Type control) {
			// TODO Auto-generated method stub
			return false;
		}

		public boolean isOpen() {
			return open;
		}

		public void open() throws LineUnavailableException {
			open(null);
		}

		public void removeLineListener(LineListener listener) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public Line getLine(javax.sound.sampled.Line.Info info)
			throws LineUnavailableException {
		return new JJackLine();
	}

	public int getMaxLines(javax.sound.sampled.Line.Info info) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Info getMixerInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info[] getSourceLineInfo() {
		return new javax.sound.sampled.Line.Info[] {
				new javax.sound.sampled.Line.Info(JJackLine.class)
		};
	}

	public javax.sound.sampled.Line.Info[] getSourceLineInfo(
			javax.sound.sampled.Line.Info info) {
		// TODO Auto-generated method stub
		return null;
	}

	public Line[] getSourceLines() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info[] getTargetLineInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info[] getTargetLineInfo(
			javax.sound.sampled.Line.Info info) {
		// TODO Auto-generated method stub
		return null;
	}

	public Line[] getTargetLines() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLineSupported(javax.sound.sampled.Line.Info info) {
		// TODO Implement proper checking
		return true;
	}

	public boolean isSynchronizationSupported(Line[] lines, boolean maintainSync) {
		// TODO Auto-generated method stub
		return false;
	}

	public void synchronize(Line[] lines, boolean maintainSync) {
		// TODO Auto-generated method stub

	}

	public void unsynchronize(Line[] lines) {
		// TODO Auto-generated method stub

	}

	public void addLineListener(LineListener listener) {
		// TODO Auto-generated method stub

	}

	public void close() {
		// TODO Auto-generated method stub

	}

	public Control getControl(Type control) {
		// TODO Auto-generated method stub
		return null;
	}

	public Control[] getControls() {
		// TODO Auto-generated method stub
		return null;
	}

	public javax.sound.sampled.Line.Info getLineInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isControlSupported(Type control) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}

	public void open() throws LineUnavailableException {
		// TODO Auto-generated method stub

	}

	public void removeLineListener(LineListener listener) {
		// TODO Auto-generated method stub

	}

}
