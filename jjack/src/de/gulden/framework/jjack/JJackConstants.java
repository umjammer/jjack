/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackConstants
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

/**
 * Constant values used by the JJack API.
 *  
 * @author  Jens Gulden
 */
public interface JJackConstants {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * Constant denoting an input buffer or port.
     *  
     * @see  JJackAudioPort
     */
    public static final int INPUT = 0;

    /**
     * Constant denoting an output buffer or port.
     *  
     * @see  JJackAudioPort
     */
    public static final int OUTPUT = 1;

    /**
     * Constant denoting the left channel in a stereo environment.
     *  
     * @see  JJackAudioEvent
     */
    public static final int LEFT = 0;

    /**
     * Constant denoting the right channel in a stereo environment.
     *  
     * @see  JJackAudioEvent
     */
    public static final int RIGHT = 1;

} // end JJackConstants
