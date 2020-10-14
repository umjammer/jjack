/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   TargetJJackLine
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Johan Salomonsen
 */

package com.petersalomonsen.jjack.javasound;

import javax.sound.sampled.TargetDataLine;

/**
 * JJack TargetDataLine implementation
 * @author Peter Johan Salomonsen
 *
 */
public class TargetJJackLine extends JJackLine implements TargetDataLine {

	public TargetJJackLine(JJackMixer mixer) {
		super(mixer);
	}

	public int read(byte[] b, int off, int len) {
		return fifo.read(b, off, len);
	}

	public int available() {
		return fifo.availableRead();
	}


	public long getLongFramePosition() {
		return fifo.getBufferPosWrite() / format.getFrameSize();
	}

	/**
	 * Used by JJackMixer to get a buffer to write float values
	 * @param length
	 * @return
	 */
	float[] getFloatBuffer(int length) {
		checkAndAllocateBuffers(length);
		return floatBuffer;
	}

	/**
	 * Used by JJackMixer to write the float buffer retrieved using getFloatBuffer()
	 *
	 */
	void writeFloatBuffer()
	{
		for(int n=0;n<floatBuffer.length;n++)
			converter.writeInt(byteBuffer,n*converter.bytesPerSample,(int)(floatBuffer[n] * (float)(1 << format.getSampleSizeInBits()-1)));
		
		fifo.write(byteBuffer, 0, byteBuffer.length);
	}
	
	boolean canWriteFloat(int length)
	{
		return fifo.availableWrite() >= length*2;
	}

}
