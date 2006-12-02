/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.SocketBeanInfo
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import java.beans.*;
import de.gulden.framework.jjack.JJackBeanInfoAbstract;

/**
 * BeanInfo class for class Socket.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class SocketBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public SocketBeanInfo() {
          	super(Socket.class, 1, 0);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = super.getPropertyDescriptors();
        try {
                  /*p[0] = new PropertyDescriptor( "name", JJackClient.class, "getName", null );
        	p[1] = new PropertyDescriptor( "running", JJackClient.class, "isRunning", "setRunning");
                  p[2] = new PropertyDescriptor( "sampleRate", JJackClient.class, "getSampleRate", null );*/
                  p[1] = new PropertyDescriptor( "sampleRate", Socket.class, "getSampleRate", null );
              }
              catch(IntrospectionException ie) {
              	exc(ie);
              }
              return p;
    }

    public int getDefaultPropertyIndex() {
        return 0;
    }

} // end SocketBeanInfo
