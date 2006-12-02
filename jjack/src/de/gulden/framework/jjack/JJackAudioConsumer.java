/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioConsumer
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

/**
 * Interface to model a <code>JJackAudioProcessor</code>'s role
 * as an exclusively monitoring client that does not generate audio output by itself.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public interface JJackAudioConsumer extends JJackAudioProcessor {
} // end JJackAudioConsumer
