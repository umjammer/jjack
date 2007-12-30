/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.ui.VUUI
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients.ui;

import de.gulden.application.jjack.clients.VU;
import de.gulden.util.swing.VUMeter;
import java.awt.*;

/**
 * User interface for class VU.
 *  
 * @author  Jens Gulden
 */
public class VUUI extends AnimatedUIAbstract {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected VU parent;

    protected VUMeter meter;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public VUUI(VU vumeter) {
        super(vumeter.getFps());
        this.setLayout(new BorderLayout());
        this.parent = vumeter;
        this.meter = new VUMeter(0.0, 1.0, 0.75, 0.9);
        this.add(this.meter, BorderLayout.SOUTH);
    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public void updateUI() {
        		if (parent != null && meter != null) {
        			int fps = parent.getFps();
        			this.setFps(fps);
        			double v = parent.getValue();
        			meter.setValue(v);
        			meter.updateUI();
        		}
    }

} // end VUUI
