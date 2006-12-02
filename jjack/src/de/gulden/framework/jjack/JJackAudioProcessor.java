/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioProcessor
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
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public interface JJackAudioProcessor {

    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    /**
     * Process multiple samples from input buffer to output buffer.
     * This is regularly called by the JACK daemon.
     *  
     * @param e event object with references to input buffer and output buffer.
     */
    public void process(JJackAudioEvent e);

} // end JJackAudioProcessor
