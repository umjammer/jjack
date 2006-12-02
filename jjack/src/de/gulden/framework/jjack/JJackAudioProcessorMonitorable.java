/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioProcessorMonitorable
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

import java.util.*;

/**
 * Extends interface <code>JJackAudioProcessor</code> by the ability
 * to add listeners that get informed about starting and ending of processing.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public interface JJackAudioProcessorMonitorable {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Adds a <code>JJackAudioProcessListener</code> to this audio processor.
     *  
     * @param l the <code>JJackAudioProcessListener</code> to add
     */
    public void addAudioProcessListener(JJackAudioProcessListener l);

    /**
     * Removes a <code>JJackAudioProcessListener</code> from this audio processor.
     *  
     * @param l the <code>JJackAudioProcessListener</code> to remove
     */
    public void removeAudioProcessListener(JJackAudioProcessListener l);

    /**
     * Returns all <code>JJackAudioProcessListener</code>s.
     *  
     * @return  Collection of <code>JJackAudioProcessListener</code>
     */
    public Collection getAudioProcessListeners();

} // end JJackAudioProcessorMonitorable
