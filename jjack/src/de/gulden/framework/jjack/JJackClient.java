/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackClient
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

/**
 * Abstract base class for JJack clients.
 * Classes derived from this one can also be used as visible JavaBeans.
 *  
 * @author  Jens Gulden
 * @see  JJackBeanInfoAbstract
 */
public abstract class JJackClient extends JJackMonitor implements JJackAudioProducer {

    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     * Creates a new instance named as the unqualified classname
     * and set to active processing mode.
     */
    public JJackClient() {
        super(true);
    }

    public JJackClient(String name) {
        super(name, true);
    }

    public JJackClient(boolean active) {
        super(active);
    }

    public JJackClient(String name, boolean active) {
        super(name, active);
    }

} // end JJackClient
