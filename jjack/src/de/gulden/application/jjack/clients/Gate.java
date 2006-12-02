/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.Gate
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

import de.gulden.application.jjack.clients.ui.GateUI;
import de.gulden.framework.jjack.*;
import de.gulden.util.nio.RingFloat;
import java.nio.FloatBuffer;
import javax.swing.*;
import java.util.*;

/**
 * JJack example client: a noise gate that suppresses audio signal below
 * a threshold value. Only if the signal is loud enoughit will be passed
 * through. (Think of this as an automatic on/off switch, reacting on the
 * incoming volume.)
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class Gate extends JJackClient {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected boolean open = false;

    protected int attack = 20;

    protected double treshold = 5.0;

    protected transient RingFloat ring = new RingFloat();

    protected transient float peak = 0.0f;

    protected transient int peakPos = 0;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public Gate() {
        super("Noise Gate");
        updateUI();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public String getInfo() {
        return "Gate - (c) Jens Gulden 2004";
    }

    /**
     *  
     * @see  de.gulden.framework.jjack.JJackAudioProcessor#process(de.gulden.framework.jjack.JJackAudioProcessEvent)
     */
    public void process(JJackAudioEvent e) {
        int attack = getAttack();
        int bufSize = JJackSystem.calculateSampleCount(attack);
        float triggerValue = (float)(0.01 * getTreshold());
        ring.ensureCapacity(bufSize);
        boolean open = isOpen();
        for (Iterator it = e.getChannels().iterator(); it.hasNext(); ) { // iterate over all channels available
        	JJackAudioChannel ch = (JJackAudioChannel)it.next();
        	FloatBuffer in = ch.getPortBuffer(INPUT);
        	FloatBuffer out = ch.getPortBuffer(OUTPUT);
        	for (int i=0; i<in.capacity(); i++) {
        		float a = in.get(i);
        		float abs = (a < 0) ? -a : a;
        		float old = ring.get(bufSize);
        		ring.put(a);
        		if ( a > peak) {
        			peak = a;
        			peakPos = 0;
        		} else if (peakPos >= bufSize) { // old peak has 'timed out'
        			peak = 0.0f;
        			for (int j = 0; j < bufSize; j++) { // find peak in buffered data
        				float b = ring.get(j);
        				if (b < 0) {
        					b = -b;
        				}
        				if ( b > peak) {
        					peak = b;
        					peakPos = j-1; // ('++' below, so -1 here)
        				}
        			}
        		}
        		open = (peak >= triggerValue);
        		out.put( i, open ? a : 0.0f );
        		peakPos++;
        	}
        	setOpen(open); // propagate last status of local variable open to property and ui
        }
    }

    /**
     */
    public int getAttack() {
        return attack;
    }

    /**
     */
    public double getTreshold() {
        return treshold;
    }

    /**
     */
    public void setAttack(int i) {
        if (attack != i) {
        	attack = i;
        	updateUI();
        }
    }

    /**
     */
    public void setTreshold(double s) {
        if (treshold != s) {
        	treshold = s;
        	updateUI();
        }
    }

    /**
     */
    public boolean isOpen() {
        return open;
    }

    /**
     */
    public void setOpen(boolean b) {
        if (open != b) {
        	open = b;
        	updateUI();
        }
    }

    protected JComponent createUI() {
        return new GateUI(this);
    }

} // end Gate
