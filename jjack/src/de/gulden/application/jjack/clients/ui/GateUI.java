/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.ui.GateUI
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients.ui;

import de.gulden.application.jjack.clients.Gate;
import de.gulden.util.swing.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

/**
 * User interface for class Gate.
 *  
 * @author  Jens Gulden
 */
public class GateUI extends JPanel {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected Gate parent;

    protected SpinButtonLabeled attackButton;

    protected SpinButtonLabeled sensitivityButton;

    protected LED led;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public GateUI(Gate gate) {
        this.parent = gate;
        setLayout(new GridLayout(1, 3, 3, 0));
        attackButton = new SpinButtonLabeled("attack (ms)", 5, 250);
        attackButton.setSensitivity(2.0f);
        attackButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setAttack( ((JSlider)(e.getSource())).getValue() );
        		}
        	}
        );
        sensitivityButton = new SpinButtonLabeled("threshold", 0.0, 100.0, 5.0, 1);
        sensitivityButton.setSensitivity(10.0f);
        sensitivityButton.addChangeListener(
        	new ChangeListener() {
        		public void stateChanged(ChangeEvent e) {
        			parent.setTreshold( ((SpinButtonLabeled)(e.getSource())).getDoubleValue() );
        		}
        	}
        );
        led = new LED(true);
        led.setBorder(new TitledBorder("signal"));
        JPanel ledPanel = new JPanel(new GridBagLayout());
        ledPanel.add(led);
        add(attackButton);
        add(sensitivityButton);
        add(ledPanel);

    }


    // ------------------------------------------------------------------------
    // --- method                                                           ---
    // ------------------------------------------------------------------------

    public void updateUI() {
        if (parent != null) {
        	int attack = parent.getAttack();
        	double sensitivity = parent.getTreshold();
        	boolean open = parent.isOpen();
        	attackButton.setValue(attack);
        	sensitivityButton.setDoubleValue(sensitivity);
        	led.setColor(open ? Color.green : Color.red);
        	led.repaint();
        }
    }

} // end GateUI
