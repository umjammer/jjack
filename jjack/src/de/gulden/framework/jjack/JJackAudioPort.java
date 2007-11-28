/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioPort
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.nio.FloatBuffer;

/**
 * Model of a port of an audio channel.
 * Every audio channel has an input port and an output port.
 *  
 * @author  Jens Gulden
 * @see  JJackAudioChannel
 * @see  JJackAudioEvent
 */
public interface JJackAudioPort {

    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    /**
     * Returns the <code>FloatBuffer</code> that holds the audio data associated with this port.
     *  
     * @return  <code>FloatBuffer</code>, either read-only (if this is an input port), or write-only (if this is an output port)
     */
    public FloatBuffer getBuffer();

} // end JJackAudioPort
