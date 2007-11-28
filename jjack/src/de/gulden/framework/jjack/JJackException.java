/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackException
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

/**
 * Exception class used by the JJack API.
 *  
 * @author  Jens Gulden
 */
public class JJackException extends Exception {

    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     * Creates a new JJackException without message text.
     */
    public JJackException() {
        super();
    }

    /**
     * Creates a new JJackException with a message text.
     *  
     * @param msg message text
     */
    public JJackException(String msg) {
        super(msg);
    }

} // end JJackException
