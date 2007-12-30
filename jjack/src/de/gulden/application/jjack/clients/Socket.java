/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.Socket
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import de.gulden.framework.jjack.*;
import de.gulden.util.Toolbox;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.*;
import java.util.*;

/**
 * JJack example client: The starting point to connect clients to when building
 * client sets inside a JavaBeans development environment.
 *  
 * @author  Jens Gulden
 */
public class Socket extends JJackClient implements ActionListener {

    // ------------------------------------------------------------------------
    // --- final static field                                               ---
    // ------------------------------------------------------------------------

    protected static final String RESOURCE_ICON = "de/gulden/application/jjack/clients/res/icons/socket.png";


    // ------------------------------------------------------------------------
    // --- field                                                            ---
    // ------------------------------------------------------------------------

    protected JPopupMenu popupMenu;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public Socket() {
        super(false);
        JJackSystem.setProcessor(this);

    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public JComponent createUI() {
        JButton button = new JButton(new ImageIcon(this.getClass().getClassLoader().getResource(RESOURCE_ICON)));
        button.setMargin(new Insets(0,0,0,0));
        button.addActionListener(this);
        return button;
    }

    public void process(JJackAudioEvent e) {
        // just copy
        for (Iterator it = e.getChannels().iterator(); it.hasNext(); ) {
        	JJackAudioChannel ch = (JJackAudioChannel)it.next();
        	FloatBuffer in = ch.getPortBuffer(INPUT);
        	FloatBuffer out = ch.getPortBuffer(OUTPUT);
        	out.rewind();
        	out.put(in);
        }
    }

    public int getSampleRate() {
        return JJackSystem.getSampleRate();
    }

    public void destroy() throws JJackException {

    }

    public void actionPerformed(ActionEvent e) {
        Toolbox.messageBox(JJackSystem.getInfo());
    }

} // end Socket
