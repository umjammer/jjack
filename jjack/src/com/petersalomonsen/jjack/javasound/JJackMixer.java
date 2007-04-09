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
		ByteBuffer byteBuffer = ByteBuffer.allocate(65536);
		
		ShortBuffer shortBuffer;
		
		byte[] buffer = byteBuffer.array();
		
		long bufferPosWrite = 0;
		long bufferPosRead = 0;

		public JJackLine()
		{
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			shortBuffer = byteBuffer.asShortBuffer();
			
			JJackSystem.setProcessor(this);
		}
		@Override
		public void process(JJackAudioEvent e) {
			for(int n=0;n<e.getOutput().capacity()*e.getOutputs().length && bufferPosRead<bufferPosWrite;n++)
			{
				e.getOutputs()[n%e.getOutputs().length].put(n/e.getOutputs().length, shortBuffer.get( (int)(bufferPosRead%buffer.length)/2  ) / 32768f);				
				bufferPosRead+=2;
			}
			releaseBlock();
		}

		public void open(AudioFormat format) throws LineUnavailableException {
			// TODO Auto-generated method stub
			
		}

		public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
			// TODO Auto-generated method stub
			
		}

		private synchronized void releaseBlock()
		{
			notify();	
		}
		
		public synchronized int write(byte[] b, int off, int len) {
			while(bufferPosWrite+len-bufferPosRead > buffer.length)
				try {
					wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
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
			// TODO Auto-generated method stub
			return 0;
		}

		public void drain() {
			// TODO Auto-generated method stub
			
		}

		public void flush() {
			// TODO Auto-generated method stub
			
		}

		public int getBufferSize() {
			// TODO Auto-generated method stub
			return 0;
		}

		public AudioFormat getFormat() {
			// TODO Auto-generated method stub
			return null;
		}

		public int getFramePosition() {
			// TODO Auto-generated method stub
			return 0;
		}

		public float getLevel() {
			// TODO Auto-generated method stub
			return 0;
		}

		public long getLongFramePosition() {
			// TODO Auto-generated method stub
			return 0;
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
			// TODO Auto-generated method stub
			return false;
		}

		public void start() {
			// TODO Auto-generated method stub
			
		}

		public void stop() {
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return false;
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
