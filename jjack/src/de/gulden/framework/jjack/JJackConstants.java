/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackConstants
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
 * Constant values used by the JJack API.
 *  
 * @author  Jens Gulden
 * @version  0.2
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
