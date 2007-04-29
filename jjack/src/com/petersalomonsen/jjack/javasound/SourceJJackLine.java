package com.petersalomonsen.jjack.javasound;
/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   SourceJJackLine
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
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import de.gulden.framework.jjack.JJackSystem;

/**
 * JJack SourceDataLine implementation
 * @author Peter Johan Salomonsen
 *
 */
public class SourceJJackLine extends JJackLine implements SourceDataLine {
		
	public void open() throws LineUnavailableException
	{
		open(null);
	}
	
	public void open(AudioFormat format) throws LineUnavailableException {
		open(format,65536);
		
	}

	public void open(AudioFormat format, int bufferSize) throws LineUnavailableException {
		fifo = new BlockingByteFIFO(bufferSize);
		
	}

	public int write(byte[] b, int off, int len) {
		return fifo.write(b, off, len);
	}

	public int available() {
		return fifo.availableWrite();
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
		return format;
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
	
	/**
	 * Used by JJackMixer to read float values
	 * @param length
	 * @return
	 */
	float[] readFloat(int length) {
		checkAndAllocateBuffers(length);
		
		fifo.read(byteBuffer.array(), 0, byteBuffer.capacity());
		
		for(int n=0;n<length;n++)
			floatBuffer[n] = shortBuffer.get(n) / 32768f;
	
		return floatBuffer;
	}

	boolean canReadFloat(int length)
	{
		return fifo.availableRead() >= length*2;
	}
}
