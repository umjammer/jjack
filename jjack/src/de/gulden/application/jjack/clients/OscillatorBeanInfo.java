/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.OscillatorBeanInfo
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

import de.gulden.framework.jjack.*;
import java.beans.*;

/**
 * BeanInfo class for class Oscillator.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class OscillatorBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public OscillatorBeanInfo() {
        super(Oscillator.class, 3, 0);
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = super.getPropertyDescriptors();
        try {
        	p[1] = new PropertyDescriptor( "zoom", Oscillator.class); //, "getZoom", "setZoom" );
        	p[2] = new PropertyDescriptor( "amplify", Oscillator.class); //, "getAmplify", "setAmplify" );
        	p[3] = new PropertyDescriptor( "fps", Oscillator.class); //, "getFps", "setFps" );
        }
        catch(IntrospectionException ie) {
        	exc(ie);
        }
        return p;
    }

} // end OscillatorBeanInfo
