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
import javax.sound.sampled.SourceDataLine;

/**
 * JJack SourceDataLine implementation
 * @author Peter Johan Salomonsen
 *
 */
public class SourceJJackLine extends JJackLine implements SourceDataLine {
		
	public SourceJJackLine(JJackMixer mixer) {
		super(mixer);
	}

	public int write(byte[] b, int off, int len) {
		return fifo.write(b, off, len);
	}

	public int available() {
		return fifo.availableWrite();
	}

	public long getLongFramePosition() {
		return fifo.getBufferPosRead() / format.getFrameSize();
	}
	
	/**
	 * Used by JJackMixer to read float values
	 * @param length
	 * @return
	 */
	float[] readFloat(int length) {
		checkAndAllocateBuffers(length);
		
		fifo.read(byteBuffer, 0, byteBuffer.length);
		
		for(int n=0;n<length;n++)
			floatBuffer[n] = (float)converter.readInt(byteBuffer, n*converter.bytesPerSample) / (1 << format.getSampleSizeInBits()-1);
	
		return floatBuffer;
	}

	boolean canReadFloat(int length)
	{
		return fifo.availableRead() >= length*2;
	}
}
