/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixerProvider
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Johan Salomonsen
 */

package com.petersalomonsen.jjack.javasound;

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

import de.gulden.framework.jjack.JJackSystem;

public class JJackMixerProvider extends MixerProvider {
	JJackMixerInfo[] infos;
	JJackMixer mixer = null;	
	
	public JJackMixerProvider()
	{
		infos = new JJackMixerInfo[] {
			JJackMixerInfo.getInfo()	
		};
		
		if(JJackSystem.isInitialized())
			mixer = new JJackMixer();
		else
			System.out.println("JACK is not available..");
	}
	
	/**
	 * @Override
	 */
	public Mixer getMixer(Info info) {
		if (! (info instanceof JJackMixerInfo) || !JJackSystem.isInitialized()) throw new IllegalArgumentException();
		return mixer;
		
	}

	/**
	 * @Override
	 */
	public Info[] getMixerInfo() {
		if(JJackSystem.isInitialized())
			return infos;
		else
			return new JJackMixerInfo[] {};
	}
}
