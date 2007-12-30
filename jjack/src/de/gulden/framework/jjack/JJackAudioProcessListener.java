/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioProcessListener
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.util.EventListener;

/**
 * Event listener that gets informed about start and end of processing.
 *  
 * @author  Jens Gulden
 */
public interface JJackAudioProcessListener extends EventListener {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Event handler method called before the supervised audio processor
     * performs its <code>process()</code>-method.
     *  
     * @param e audio event that is going to be processed by the supervised audio processor
     */
    public void beforeProcess(JJackAudioEvent e);

    /**
     * Event handler method called after the supervised audio processor
     * has performed its <code>process()</code>-method.
     *  
     * @param e audio event that has been processed by the supervised audio processor
     */
    public void afterProcess(JJackAudioEvent e);

} // end JJackAudioProcessListener
