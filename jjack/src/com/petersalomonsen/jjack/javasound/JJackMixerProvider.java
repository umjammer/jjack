package com.petersalomonsen.jjack.javasound;

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixerProvider
 * Version: 0.3
 *
 * Date:    2007-04-09
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

public class JJackMixerProvider extends MixerProvider {
	JJackMixerInfo[] infos;
	JJackMixer mixer;
	
	class JJackMixerInfo extends Info
	{

		protected JJackMixerInfo(String name, String vendor, String description, String version) {
			super(name, vendor, description, version);
		}		
	}
	
	
	public JJackMixerProvider()
	{
		infos = new JJackMixerInfo[] {
			new JJackMixerInfo("JJack","jjack.berlios.de","JJack javasound provider","0.1")	
		};
		
		mixer = new JJackMixer();
	}
	
	/**
	 * @Override
	 */
	public Mixer getMixer(Info info) {
		//assert(info instanceof JJackMixerInfo);
		if (! (info instanceof JJackMixerInfo) ) throw new InternalError("assertion failed");
		return mixer;
	}

	/**
	 * @Override
	 */
	public Info[] getMixerInfo() {
		return infos;
	}

}
