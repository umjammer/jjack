package com.petersalomonsen.jjack.javasound;
/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   ByteIntConverter
 * Version: 0.3
 *
 * Date:    2007-05-06
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

/**
 * Convert 8,16,24 and 32 bit integers (stored as bytes) to and from integer variables
 * 
 * @author Peter Johan Salomonsen
 */
public final class ByteIntConverter {

	/**
	 * Use final for inline optimization
	 */
	final int bytesPerSample;
	final boolean bigEndian;
	final boolean signed;
	final int msbIndex;
	
	public ByteIntConverter(int bytesPerSample,boolean bigEndian,boolean signed)
	{
		this.bytesPerSample = bytesPerSample;
		this.bigEndian = bigEndian;
		this.signed = signed;
		msbIndex = bigEndian ? 0 : bytesPerSample - 1;
	}
	
	/**
	 * Read from the byte array into an integer
	 * @param bytes
	 * @param index
	 * @return
	 */
	public final int readInt(final byte[] bytes,int index)
	{
		int val = 0;

		for(int n=0;n<bytesPerSample;n++)
		{
			val |= 		(bytes[index++] & (n==msbIndex && signed ? -1 : 0xff)) 
					<< ( (bigEndian ? bytesPerSample-1-n : n) * 8);
		}

		if(signed)
			return val;// / (1 << (bytesPerSample*8));
		else
			return val + (1 << ((bytesPerSample*8)-1));
	}
	
	/**
	 * Write an integer into the byte array
	 * @param bytes
	 * @param index
	 * @param value
	 */
	public final void writeInt(final byte[] bytes, int index, int value)
	{
		for(int n=0;n<bytesPerSample;n++)
		{
			final int shift = ( (bigEndian ? bytesPerSample-1-n : n) * 8);
			bytes[index++] = (byte)((value & (0xff << shift)) >> shift);
		}		
	}
}
