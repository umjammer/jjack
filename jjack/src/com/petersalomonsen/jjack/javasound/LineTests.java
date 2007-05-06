package com.petersalomonsen.jjack.javasound;
/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   ByteIntConverterTest
 * Version: 0.3
 *
 * Date:    2007-05-06
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */


import javax.sound.sampled.AudioFormat;


/**
 * Test class for the Source and Target datalines. Will test all audioformats used by the javasound jjack impl, and verify
 * that a full throughput of data results in the same values in the other end.
 * 
 * @author Peter Johan Salomonsen
 *
 */
class LineTests {
	public static void main(String[] args) throws Exception
	{
		
		AudioFormat[] audioFormats = new AudioFormat[8];
		
		// Fill audioformat array
		for(int n=0;n<audioFormats.length;n++)
			audioFormats[n] = new AudioFormat(44100,8+(8*(n%4)),((n/8)+1),true,((n%8)/4) == 0 ? false : true);
		
		for(int fmIndex=0;fmIndex<audioFormats.length;fmIndex++)
		{
			AudioFormat fmt = audioFormats[fmIndex];
			System.out.println(fmt);

			SourceJJackLine src = new SourceJJackLine();
			src.open(fmt);
			
			TargetJJackLine tgt = new TargetJJackLine();
			tgt.open(fmt);

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
				
				src.write(b, n, conv.bytesPerSample);
				System.out.print(", ");
				
				float flVal = src.readFloat(1)[0];
				tgt.getFloatBuffer(1)[0] = flVal;
				tgt.writeFloatBuffer();
				System.out.print(" float Value "+flVal+" ");
			
				tgt.read(b, n, conv.bytesPerSample);
				
				long ret = conv.readInt(b, n);
				
				System.out.println(ret+" "+(ret==val));
			}
		}
	}
}
