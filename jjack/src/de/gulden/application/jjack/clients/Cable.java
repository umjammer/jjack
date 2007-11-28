/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.Cable
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import de.gulden.framework.jjack.*;
import java.nio.FloatBuffer;

/**
 * JJack example client: Passes the audio signal through without any change.
 * This is just a null-client.
 *  
 * @author  Jens Gulden
 */
public class Cable extends Socket {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     */
    public Cable() {
        super();
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public void processSignal(JJackAudioEvent e) {
        FloatBuffer in = e.getInput();
        FloatBuffer out = e.getOutput();
        out.put(in); // :-)
    }

} // end Cable
