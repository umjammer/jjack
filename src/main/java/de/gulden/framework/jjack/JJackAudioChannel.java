/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioChannel
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.nio.FloatBuffer;

/**
 * Model of an audio channel.
 * Every audio channel has an input port and an output port.
 *  
 * @author  Jens Gulden
 * @see  JJackAudioPort
 * @see  JJackAudioEvent
 */
public interface JJackAudioChannel {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the input or output port of this channel.
     *  
     * @param port either constant <code>INPUT</code> or <code>OUTPUT</code>
     * @return  the input or output port, as requested
     */
    public JJackAudioPort getPort(int port);

    /**
     * Returns the input or output buffer of this channel.
     * This is a convenience method for <code>getPort(port).getBuffer()</code>.
     *  
     * @param port either constant <code>INPUT</code> or <code>OUTPUT</code>
     * @return  the audio data buffer
     */
    public FloatBuffer getPortBuffer(int port);

    /**
     * Returns the index number of this channel.
     * In stereo configurations (default), the returned value is either constant <code>LEFT</code>(=0) or <code>RIGHT</code>(=1).
     *  
     * @return  the index number of the channel
     */
    public int getIndex();

} // end JJackAudioChannel
