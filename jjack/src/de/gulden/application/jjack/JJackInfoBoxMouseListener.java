/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.JJackInfoBoxMouseListener
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack;

import de.gulden.framework.jjack.JJackMonitor;
import de.gulden.util.Toolbox;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPopupMenu;

/**
 * MouseListener class to handle info-box menu and displaying when right-clicking on a client's panel.
 *  
 * @author  Jens Gulden
 */
class JJackInfoBoxMouseListener extends MouseAdapter implements ActionListener {

    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------

    /**
     * The JJack client whose gui panel's mouse-clicks are handled by this listener.
     */
    protected JJackMonitor client;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     *  
     * @param client the JJack client whose gui panel's mouse-clicks are handled by this listener
     */
    JJackInfoBoxMouseListener(JJackMonitor client) {
        this.client = client;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *  
     * @see  java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        String info = client.getInfo();
        if ((info != null) && (info.length() > 0)) {
        	if (e.isMetaDown()) {
        		JPopupMenu menu = new JPopupMenu();
        		menu.add("Info...").addActionListener(this);
        		menu.show((Component)e.getSource(), e.getX(), e.getY());
        	} else {
        		if (e.getClickCount()==2) {
        			info();
        		}
        	}
        }
    }

    /**
     * Invoked when an action occurs.
     *  
     * @see  java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        info();
    }

    /**
     * Displays info on the JJack client in a message box.
     */
    protected void info() {
        String name = client.getName();
        String info = client.getInfo();
        Toolbox.messageBox(name, info);
    }

} // end JJackInfoBoxMouseListener
