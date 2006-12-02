/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.DelayBeanInfo
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

import de.gulden.framework.jjack.JJackBeanInfoAbstract;
import java.beans.*;

/**
 * BeanInfo class for class Delay.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class DelayBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public DelayBeanInfo() {
        super(Delay.class, 5, 0);
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = super.getPropertyDescriptors();
        try {
        	p[1] = new PropertyDescriptor( "time", Delay.class);
        	p[2] = new PropertyDescriptor( "mixSignal", Delay.class);
        	p[3] = new PropertyDescriptor( "mixFx", Delay.class);
        	p[4] = new PropertyDescriptor( "outSignal", Delay.class);
        	p[5] = new PropertyDescriptor( "outFx", Delay.class);
        }
        catch(IntrospectionException ie) {
        	exc(ie);
        }
        return p;
    }

} // end DelayBeanInfo
