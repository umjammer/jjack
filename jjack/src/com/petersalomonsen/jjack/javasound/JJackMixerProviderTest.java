package com.petersalomonsen.jjack.javasound;

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixerProviderTest
 * Version: 0.3
 *
 * Date:    2007-04-09
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.Mixer.Info;

public class JJackMixerProviderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// Scan mixers
		Info jackMixerInfo = null;
		
		for(Info info : AudioSystem.getMixerInfo())
		{
			if(info.getName().equals("JJack"))
				jackMixerInfo = info;
		}
		
		AudioInputStream stream = AudioSystem.getAudioInputStream(new File("/home/peter/mystudio/teaparty/teaparty.wav"));
		SourceDataLine line = (SourceDataLine) AudioSystem.getMixer(jackMixerInfo).getLine(null);
		line.open();
		line.start();
		
		byte[] buf = new byte[128];
		while(stream.available()>0)
		{
			stream.read(buf,0,buf.length);
			line.write(buf,0,buf.length);
		}
			
	}
}
