/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.Delay
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients;

import de.gulden.application.jjack.clients.ui.DelayUI;
import de.gulden.framework.jjack.*;
import de.gulden.util.nio.RingFloat;
import java.nio.FloatBuffer;
import javax.swing.*;

/**
 * JJack example client: add an echo effect to the audio signal.
 *  
 * @author  Jens Gulden
 */
public class Delay extends JJackClient {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected int time = 250;

    protected int mixSignal = 75;

    protected int mixFx = 50;

    protected int outSignal = 75;

    protected int outFx = 50;

    protected RingFloat[] ring;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public Delay() {
        super();
        ring = null; // init in first call of process() when #channels is known
        updateUI();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public String getInfo() {
        return "Delay - (c) Jens Gulden 2004";
    }

    /**
     *  
     * @see  de.gulden.framework.jjack.JJackAudioProcessor#process(de.gulden.framework.jjack.JJackAudioEvent)
     */
    public void process(JJackAudioEvent e) {
        int delaytime = getTime();
        float mixSignal = (float)getMixSignal() / 100;
        float mixFx = (float)getMixFx() / 100;
        float outSignal = (float)getOutSignal() / 100;
        float outFx = (float)getOutFx() / 100;
        int sampleRate = getSampleRate();
        int diff = delaytime * sampleRate / 1000 ;
        int channels = e.countChannels(); // number of channels (assumes same number of input and output ports)
        if (ring == null) { // first call, init ringbuffers for each channel
        	  ring = new RingFloat[channels];
        	  for (int i = 0; i < channels; i++) {
        	  	ring[i] = new RingFloat(diff);
        	  }
        }
        for (int i=0; i < channels; i++) {
        	RingFloat r = ring[i];
        	r.ensureCapacity(diff);
        	FloatBuffer in = e.getInput(i);      // input buffer
        	FloatBuffer out = e.getOutput(i); // output buffer
        	int cap = in.capacity(); // number of samples are available
        	for (int j=0; j<cap; j++) {
        		float signal = in.get(j); // read input signal
        		float fx = r.get(diff);
        		float mix = signal * mixSignal + fx * mixFx;
        		float ou = signal * outSignal + fx * outFx;
        		r.put(mix); // remember for delay
        		out.put(j, ou); // write input signal
        	}
        }
    }

    /**
     */
    public int getTime() {
        return time;
    }

    /**
     */
    public int getMixFx() {
        return mixFx;
    }

    /**
     */
    public int getMixSignal() {
        return mixSignal;
    }

    /**
     */
    public int getOutFx() {
        return outFx;
    }

    /**
     */
    public int getOutSignal() {
        return outSignal;
    }

    /**
     */
    public void setTime(int i) {
        time = i;
        updateUI();
    }

    /**
     */
    public void setMixFx(int i) {
        mixFx = i;
        updateUI();
    }

    /**
     */
    public void setMixSignal(int i) {
        mixSignal = i;
        updateUI();
    }

    /**
     */
    public void setOutFx(int i) {
        outFx = i;
        updateUI();
    }

    /**
     */
    public void setOutSignal(int i) {
        outSignal = i;
        updateUI();
    }

    protected JComponent createUI() {
        return new DelayUI(this);
    }

} // end Delay
