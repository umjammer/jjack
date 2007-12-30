/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioConsumer
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

/**
 * Interface to model a <code>JJackAudioProcessor</code>'s role
 * as an exclusively monitoring client that does not generate audio output by itself.
 *  
 * @author  Jens Gulden
 */
public interface JJackAudioConsumer extends JJackAudioProcessor {
} // end JJackAudioConsumer
