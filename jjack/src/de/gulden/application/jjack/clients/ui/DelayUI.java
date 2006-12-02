/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.ui.DelayUI
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients.ui;

import de.gulden.application.jjack.clients.Delay;
import de.gulden.util.swing.SpinButtonLabeled;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * User interface for class Delay.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class DelayUI extends JPanel {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected Delay parent;

    protected SpinButtonLabeled delaytimeButton;

    protected SpinButtonLabeled mixSignalButton;

    protected SpinButtonLabeled mixFxButton;

    protected SpinButtonLabeled outSignalButton;

    protected SpinButtonLabeled outFxButton;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public DelayUI(Delay delay) {
        this.parent = delay;
        setLayout(new GridLayout(1, 5));
        delaytimeButton = new SpinButtonLabeled("time (ms)", 5, 10000);
        delaytimeButton.setSensitivity(10.0f);
        delaytimeButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setTime(((JSlider)(e.getSource())).getValue());
        		}
        	}
        );
        mixSignalButton = new SpinButtonLabeled("mix signal", true);
        mixSignalButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setMixSignal( ((JSlider)(e.getSource())).getValue() );
        		}
        	}
        );
        mixFxButton = new SpinButtonLabeled("mix fx", true);
        mixFxButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setMixFx( ((JSlider)(e.getSource())).getValue() );
        		}
        	}
        );
        outSignalButton = new SpinButtonLabeled("out signal", true);
        outSignalButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setOutSignal( ((JSlider)(e.getSource())).getValue() );
        		}
        	}
        );
        outFxButton = new SpinButtonLabeled("out fx", true);
        outFxButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setOutFx( ((JSlider)(e.getSource())).getValue() );
        		}
        	}
        );
        add(delaytimeButton);
        add(mixSignalButton);
        add(mixFxButton);
        add(outSignalButton);
        add(outFxButton);

    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public void updateUI() {
        super.updateUI();
        if (parent != null) {
        	if (delaytimeButton != null) {
        		delaytimeButton.setValue(parent.getTime());
        	}
        	if (mixSignalButton != null) {
        		mixSignalButton.setValue(parent.getMixSignal());
        	}
        	if (mixFxButton != null) {
        		mixFxButton.setValue(parent.getMixFx());
        	}
        	if (outSignalButton != null) {
        		outSignalButton.setValue(parent.getOutSignal());
        	}
        	if (outFxButton != null) {
        		outFxButton.setValue(parent.getOutFx());
        	}
        }
    }

} // end DelayUI
