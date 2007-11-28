/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.OscilloscopeBeanInfo
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import de.gulden.framework.jjack.*;
import java.beans.*;

/**
 * BeanInfo class for class Oscilloscope.
 *  
 * @author  Jens Gulden
 */
public class OscilloscopeBeanInfo extends JJackBeanInfoAbstract {

    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public OscilloscopeBeanInfo() {
        super(Oscilloscope.class, 3, 0);
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = super.getPropertyDescriptors();
        try {
        	p[1] = new PropertyDescriptor( "zoom", Oscilloscope.class); //, "getZoom", "setZoom" );
        	p[2] = new PropertyDescriptor( "amplify", Oscilloscope.class); //, "getAmplify", "setAmplify" );
        	p[3] = new PropertyDescriptor( "fps", Oscilloscope.class); //, "getFps", "setFps" );
        }
        catch(IntrospectionException ie) {
        	exc(ie);
        }
        return p;
    }

} // end OscilloscopeBeanInfo
