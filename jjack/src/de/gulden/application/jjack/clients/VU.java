/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.VU
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

import de.gulden.application.jjack.clients.ui.VUUI;
import de.gulden.framework.jjack.*;
import java.nio.FloatBuffer;
import javax.swing.*;

/**
 * JJack example client: Displays the audio signal level as a chain of green,
 * yellow and red LEDs.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class VU extends JJackMonitor {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected double value = 0.0;

    protected int fps = 25;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public String getInfo() {
        return "VUMeter - (c) Jens Gulden 2004";
    }

    /**
     *  
     * @see  de.gulden.framework.jjack.JJackAudioProcessor#process(de.gulden.framework.jjack.JJackAudioProcessEvent)
     */
    public void process(JJackAudioEvent e) {
        FloatBuffer buf = e.getInput();
        for (int i=0; i < buf.capacity(); i++) {
        	 double a = buf.get(i);
        	 if (a < 0) {
        	 	a = -a;
        	 }
        	 if (a > this.value) {
        	 	this.value = a; // collect maximum peak in this.value
        	 }
        }
    }

    /**
     */
    public int getFps() {
        return fps;
    }

    /**
     */
    public double getValue() {
        double v = value;
        value = 0.0;
        return v;
    }

    /**
     */
    public void setFps(int i) {
        fps = i;
    }

    protected JComponent createUI() {
        return new VUUI(this);
    }

} // end VU
