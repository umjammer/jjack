/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioProducer
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.util.Collection;

/**
 * Interface to model a <code>JJackAudioProcessor</code>'s role
 * as a client that generates audio output.
 * Other audio processors can be connected via the <code>monitor</code>- and <code>chained</code>-event-sets.
 *  
 * @author  Jens Gulden
 */
public interface JJackAudioProducer extends JJackAudioProcessor {

    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Adds a monitor processor after this processor.
     * The monitor processor will get the output of this processor as its own input.
     *  
     * @param c the monitoring audio processor to be added
     */
    public void addMonitor(JJackAudioConsumer c);

    /**
     * Removes a monitor processor from this processor.
     *  
     * @param c the monitoring audio processor to be removed
     */
    public void removeMonitor(JJackAudioConsumer c);

    /**
     * Returns all monitor processors currently connected after this processor.
     *  
     * @return  Collection of JJackAudioConsumer
     */
    public Collection getMonitors();

    /**
     * Sets the next audio processor in chain.
     * The chained processor will get the output of this processor as its own input.
     *  
     * @param p the next audio processor in chain
     */
    public void setChained(JJackAudioProducer p);

    /**
     * Removes <code>p</code> as the next audio processor in chain,
     * if it is the currently chained processor.
     *  
     * @param p the current audio processor in chain
     */
    public void removeChained(JJackAudioProducer p);

    /**
     * Returns the next audio processor in chain.
     *  
     * @return  the next audio processor in chain, <code>null</code> if no processor is chained to <code>this</code>
     */
    public JJackAudioProducer getChained();

} // end JJackAudioProducer
