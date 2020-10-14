/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.ChannelBeanInfo
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import de.gulden.framework.jjack.JJackBeanInfoAbstract;
import java.beans.*;

/**
 * BeanInfo class for class Channel.
 *  
 * @author  Jens Gulden
 */
public class ChannelBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public ChannelBeanInfo() {
        super(Channel.class, 1, 0);
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = super.getPropertyDescriptors();
        try {
        	p[1] = new PropertyDescriptor( "channel", Channel.class);
        }
        catch(IntrospectionException ie) {
        	exc(ie);
        }
        return p;
    }

} // end ChannelBeanInfo
