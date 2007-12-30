/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixerProviderTest
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Johan Salomonsen
 */

package com.petersalomonsen.jjack.javasound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer.Info;

import de.gulden.framework.jjack.JJackSystem;

/**
 * Simple test that reads the input and writes to the output - the default buffer size of 64KB will create a delay effect
 * 
 * @author Peter Johan Salomonsen
 *
 */
public class JJackMixerProviderTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// Scan mixers
		Info jackMixerInfo = null;
		
		//for(Info info : AudioSystem.getMixerInfo())
		Info[] infos = AudioSystem.getMixerInfo();
		for (int i = 0; i < infos.length; i++)
		{
			Info info = infos[i];
			System.out.println(info.getName());
			if(info.getName().equals("JJack"))
				jackMixerInfo = info;
		}
		AudioFormat format = new AudioFormat(JJackSystem.getSampleRate(),16,2,true,false);
		
		Mixer mixer = AudioSystem.getMixer(jackMixerInfo);
		SourceDataLine lineOut = (SourceDataLine) mixer.getLine(mixer.getSourceLineInfo()[0]);
		lineOut.open(format,512);
		lineOut.start();
		TargetDataLine lineIn = (TargetDataLine) mixer.getLine(mixer.getTargetLineInfo()[0]);
		lineIn.open(format,512);
		lineIn.start();
		
		byte[] buf = new byte[128];
		while(true)
		{
			lineIn.read(buf,0,buf.length);
			lineOut.write(buf,0,buf.length);
		}
			
	}
}
