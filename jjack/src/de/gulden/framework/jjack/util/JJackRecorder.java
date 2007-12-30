/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.util.JJackRecorder
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack.util;

import de.gulden.framework.jjack.JJackAudioEvent;
import de.gulden.framework.jjack.JJackAudioProcessor;
import de.gulden.framework.jjack.JJackSystem;
import java.nio.FloatBuffer;

/**
 * A client for recording audio data into a memory buffer.
 * Also provides static functions for converting audio data.
 * 
 * @author Jens Gulden
 */
public class JJackRecorder implements JJackAudioProcessor {
	
	private float[][] data;
	private int pos = 0;
	private boolean recording = true;

	public JJackRecorder(int seconds) {
		this(2, 10); // default: 10 secs stereo at default sample-rate
	}
	
	public JJackRecorder(int channels, int seconds) {
		data = new float[2][JJackSystem.getSampleRate() * channels * seconds]; // default: 10 secs stereo at default sample-rate
	}
	
	public JJackRecorder(float[][] data) {
		this.data = data;
	}

	public void process(JJackAudioEvent e) {
		int all = data[0].length;
		if (pos >= all) { // buffer is full
			recording = false;
			return;
		}
		FloatBuffer[] b = e.getInputs();
		int period = b[0].capacity();
		int rest = all - pos;
		if (period > rest) {
			period = rest;
		}
		for (int channel = 0; channel < data.length; channel++) {
			if (channel < b.length) {
				b[channel].get(data[channel], pos, period);
			}
		}
		pos += period;
	}
	
	public int getPosition() {
		return pos;
	}
	
	public boolean isRecording() {
		return recording;
	}
	
	public static short[][] floatToPCM(float[][] data) {
		short[][] pcm = new short[data.length][];
		for (int i = 0; i < data.length; i++) {
			int l = data[i].length;
			pcm[i] = new short[l];
			for (int j = 0; j < l; j++) {
				pcm[i][j] = (short)(data[i][j] * Short.MAX_VALUE);
			}
		}
		return pcm;
	}
	
	public static short[] interleavePCM(short[][] pcm) {
		int channels = pcm.length;
		int l = pcm[0].length;
		short[] stereo = new short[channels * l]; //usually done for stereo, but works for more channels, too
		int ii = 0;
		for (int i = 0; i < stereo.length; i+=channels) {
			for (int j = 0; j < channels; j++) {
				short f = pcm[j][ii];
				stereo[i + j] = f;
			}
			ii++;
		}
		return stereo;
	}
	
	public static byte[] encodeBytes(short[] pcm, boolean signed, boolean bigEndian) {
		// (number of channels doesn't matter for this algorithm)
		byte[] b = new byte[pcm.length * 2];
		int k = 0;
		for (int i = 0; i < pcm.length; i++) {
			int sample = pcm[i]; // originally signed
			if (! signed) {
				sample -= Short.MIN_VALUE;
			}
			byte little = (byte)(sample & 0xff);
			byte big = (byte)((sample>>8) & 0xff);
			if (bigEndian) {
				b[k++] = big;
				b[k++] = little;
			} else {
				b[k++] = little;
				b[k++] = big;
			}
		}
		return b;
	}
	
	public static float[] downmixMono(float[][] data) {
		int channels = data.length;
		int l = data[0].length;
		float[] mono = new float[l];
		for (int i = 0; i < l; i++) {
			float sum = data[0][i];
			for (int channel = 1; channel < channels; channel++) {
				sum += data[channel][i];
			}
			mono[i] = sum / channels;
		}
		return mono;
	}
	
	public static float[][] cut(float[][] data, int startFrame, int endFrame) {
		int len = endFrame - startFrame;
		if (len < 0) len = 0;
		float[][] result = new float[data.length][];
		for (int channel = 0; channel < data.length; channel++) {
			result[channel] = new float[len];
			System.arraycopy(data[0], startFrame, result[channel], 0, len);
		}
		return result;
	}
}
