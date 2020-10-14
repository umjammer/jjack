/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.ui.OscilloscopeUI
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack.clients.ui;

import de.gulden.application.jjack.clients.Oscilloscope;
import java.awt.*;
import java.nio.FloatBuffer;

/**
 * User interface for class Oscilloscope.
 *  
 * @author  Jens Gulden
 */
public class OscilloscopeUI extends AnimatedUIAbstract {

    // ------------------------------------------------------------------------
    // --- final static field                                               ---
    // ------------------------------------------------------------------------

    protected static final int RING_SIZE = 10000;


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected Oscilloscope parent;

    protected int[] ring = new int[RING_SIZE];

    protected int ringPos = 0;

    protected float[] restBuf = new float[0];


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public OscilloscopeUI(Oscilloscope oscilloscope) {
        super(oscilloscope.getFps());
        this.parent = oscilloscope; // oscilloscope displayed by this
        setBackground(new Color(255, 255, 255));
        setMinimumSize(new Dimension(100, 50));
        setPreferredSize(new Dimension(300, 100));

    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Called by the process()-thread.
     */
    public void output(FloatBuffer buf) {
        int[] y;
        synchronized (restBuf) {
        	Dimension d = this.getSize();
        	int amplify = parent.getAmplify();
        	if (amplify == -1) { // auto-amplify to total component height if -1
        		amplify = d.height / 2;
        	}
        	int samples = buf.capacity() + restBuf.length;
        	float zoom = parent.getZoom();
        	int x = (int)(samples * zoom);
        	int samplespercol = (int) (1.0 / zoom);
        	y = new int[x];
        	int restbufPos = 0;
        	int i = 0;
        	while (samples>=samplespercol) { // repeat while complete pixel columns can be calculated
        		float[] colsamples = new float[samplespercol];
        		int colsamplesPos = 0;
        		if (restBuf.length>0) {
        			if (restBuf.length-restbufPos<=samplespercol) {
        				int l = restBuf.length-restbufPos;
        				System.arraycopy(restBuf, restbufPos, colsamples, colsamplesPos, l);
        				samples -= l;
        				colsamplesPos += l;
        				restBuf = new float[0];
        			} else { // more in restBuf than to be output for a single col (can happen if zoom-factor or component size has changed)
        				int l = colsamples.length-colsamplesPos;
        				System.arraycopy(restBuf, restbufPos, colsamples, colsamplesPos, l);
        				samples -= l;
        				colsamplesPos = samplespercol; // full
        				restbufPos += l;
        			}
        		}
        		int l = samplespercol - colsamplesPos;
        		if (l > 0) { // rest buffer is used up and we still need some data for pixel col (and it is surely available due to while-cond.)
        			buf.get(colsamples, colsamplesPos, l);
        			samples -= l;
        		}
        		// colsamples is now filled
        		float avg = average(colsamples); // average value for a single pixel col
        		y[i++] = (int) (avg * amplify);
        	}
        	// y[] is now filled, store rest in restBuf
        	float[] newRestBuf = new float[samples];
        	int l = restBuf.length - restbufPos;
        	if (l > 0) {
        		System.arraycopy(restBuf, restbufPos, newRestBuf, 0, l);
        		samples -= l;
        	} else {
        		l = 0;
        	}
        	buf.get(newRestBuf, l, samples); // buf now exactly empty
        	restBuf = newRestBuf;
        }

        // add new graph values to ringbuffer
        synchronized (ring) {
        	int remain = RING_SIZE - ringPos;
        	if (y.length > remain) {
        		System.arraycopy(y, 0, ring, ringPos, remain );
        		ringPos = y.length-remain;
        		System.arraycopy(y, remain, ring, 0, ringPos );
        	} else {
        		System.arraycopy(y, 0, ring, ringPos, y.length );
        		ringPos+=y.length;
        	}
        }
    }

    public void updateUI() {
        repaint();
    }

    /**
     */
    public void paint(Graphics g) {
        Dimension r = getSize();
        g.clearRect(0, 0, r.width, r.height);
        int mid = r.height / 2;
        g.setColor(Color.blue);
        int n = r.width;
        g.drawLine(0, mid, n, mid);
        g.setColor(Color.black);
        int[] x = new int[n];
        int[] y = new int[n];
        synchronized (ring) {
        	for (int i=0; i<n; i++) {
        		x[i] = i;
        		y[i] = mid + ring[ ( ringPos + i - n + RING_SIZE ) % RING_SIZE ];
        	}
        }
        g.drawPolyline(x, y, n);
    }

    private float average(float[] f) {
        double sum = 0.0;
        int total = f.length;
        for (int i=0; i<f.length; i++) {
        	float ff=f[i];
        	if ( ! Float.isNaN(ff)) {
        		sum += f[i];
        	} else {
        		total--;
        	}
        }
        return (float) (sum/total);
    }

} // end OscilloscopeUI
