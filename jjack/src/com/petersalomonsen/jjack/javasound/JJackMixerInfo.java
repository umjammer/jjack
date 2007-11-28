/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackMixer
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

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
