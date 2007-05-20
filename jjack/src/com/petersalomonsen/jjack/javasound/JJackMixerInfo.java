package com.petersalomonsen.jjack.javasound;

import javax.sound.sampled.Mixer.Info;

class JJackMixerInfo extends Info
{
	protected JJackMixerInfo(String name, String vendor, String description, String version) {
		super(name, vendor, description, version);
	}
	
	static JJackMixerInfo info = new JJackMixerInfo("JJack","jjack.berlios.de","JJack javasound provider","0.1");
	
	static JJackMixerInfo getInfo()
	{
		return info;
	}
}
