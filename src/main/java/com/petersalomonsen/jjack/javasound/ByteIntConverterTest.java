/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   ByteIntConverterTest
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Johan Salomonsen
 */

package com.petersalomonsen.jjack.javasound;

import javax.sound.sampled.AudioFormat;


/**
 * Test class for the ByteIntConverter. Will test all audioformats used by the javasound jjack impl.
 * 
 * @author Peter Johan Salomonsen
 *
 */
class ByteIntConverterTest {
	public static void main(String[] args)
	{
		
		AudioFormat[] audioFormats = new AudioFormat[8];
		
		// Fill audioformat array
		for(int n=0;n<audioFormats.length;n++)
			audioFormats[n] = new AudioFormat(44100,8+(8*(n%4)),((n/8)+1),true,((n%8)/4) == 0 ? false : true);
		
		for(int fmIndex=0;fmIndex<audioFormats.length;fmIndex++)
		{
			AudioFormat fmt = audioFormats[fmIndex];
			System.out.println(fmt);
			ByteIntConverter conv = new ByteIntConverter(fmt.getSampleSizeInBits()/8,fmt.isBigEndian(),
					fmt.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ? true : false
					);
			 
			final int numValues = 8;
			int byteArrLen = numValues*fmt.getSampleSizeInBits()/8;
			byte[] b = new byte[byteArrLen];
			for(int n=0;n<byteArrLen;n+=fmt.getSampleSizeInBits()/8)
			{
				long val = ((n / (fmt.getSampleSizeInBits()/8) ) * (1l<<fmt.getSampleSizeInBits()) / numValues) - (1l<<fmt.getSampleSizeInBits() -1);
				System.out.print(val);
				conv.writeInt(b, n, (int)val);
				System.out.print(" ");
				
				long ret = conv.readInt(b, n);
				System.out.println(ret+" "+(ret==val));
			}
		}
	}
}
