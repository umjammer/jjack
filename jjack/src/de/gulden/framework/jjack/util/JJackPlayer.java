/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.util.JJackPlayer
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack.util;

import java.nio.FloatBuffer;

import de.gulden.framework.jjack.JJackAudioEvent;
import de.gulden.framework.jjack.JJackAudioProcessor;

/**
 * A client for outputting audio data, either from a memory buffer or 
 * algorithmically generated.
 * 
 * @author Jens Gulden
 */
public class JJackPlayer implements JJackAudioProcessor {
	
	private float[][] data = null;
	private float[] monoData = null;
	private int pos = 0;
	private boolean playing = true;
	private int avail = -1;
	private boolean oneShot = false; 

	public JJackPlayer() {
		// nop (both available() and getSampleAt() should be overwritten when using this constructor)
	}

	public JJackPlayer(int availableSamples, boolean oneShot) {
		// (getSampleAt() should be overwritten when using this constructor)
		this.avail = availableSamples;
		this.oneShot = oneShot;
	}

	public JJackPlayer(float[][] data) {
		this.data = data;
	}

	public JJackPlayer(float[] monoData) {
		this.monoData = monoData;
	}

	public boolean isOneShot() {
		return oneShot;
	}

	public void setOneShot(boolean oneShot) {
		this.oneShot = oneShot;
	}

	public boolean isPlaying() {
		return playing;
	}

	public void process(JJackAudioEvent e) {
    	int a = available();
		int channels = e.countOutputPorts();
    	int period = e.getOutput(0).capacity(); // TODO in JJack: e.getPeriodSize() or so
    	int all;
    	if (data != null) {
    		all = data[0].length;
    	} else if (monoData != null) {
    		all = monoData.length;
    	} else {
    		all = 0;
    	}
    	int rest = all - pos;
		for (int channel = 0; channel < channels; channel++) {
        	FloatBuffer out = e.getOutput(channel); // output buffer
        	if (period <= rest) { // period fits completely into buffer: optimized buffer-copy
        		// assert (data != null) || (monoData != null);
            	if (data != null) {
            		out.put( data[channel], pos, period );
            	} else if (monoData != null) {
            		out.put( monoData, pos, period );
            	}
        	} else { // always if buffers are null (overwritte get(Mono)SampleAt())
        		for (int i = 0; i < period; i++) {
        			float f;
        			int pos = this.pos + i;
        			if ((a != -1) && (pos > a)) { // waveform has an end and it has been reached
        				if ( oneShot ) {
        					f = 0f;
        					this.playing = false;
        				} else {
        					pos = pos % a;
                			f = getSampleAt(channel, pos);
        				}
        			} else { // end not reached
            			f = getSampleAt(channel, pos);
        			}
        			out.put(i, f);
        		}
        	}
		}
    	pos += period;
	}
	
	public int available() {
		if (data != null) {
			return data[0].length;
		} else if (monoData != null) {
			return monoData.length;
		} else {
			return avail; // -1 by default: infinite
		}
	}

	public float getSampleAt(int channel, int pos) {
		// default implementation, might be overwritten by subclasses (e.g. to generate a waveform algorithmically instead of reading from a buffer)
		if (data != null) {
			return data[channel][pos];
		} else {
			return getMonoSampleAt(pos);
		}
	}
	
	protected float getMonoSampleAt(int pos) {
		// default implementation, might be overwritten by subclasses (e.g. to generate a waveform algorithmically instead of reading from a buffer)
		return monoData[pos];
	}
}
