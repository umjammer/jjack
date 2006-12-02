/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.clients.ui.AnimatedUIAbstract
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

import java.io.Serializable;
import javax.swing.JPanel;

/**
 * Abstract base class for all user interface classes that handle animation.
 * Handling of the frames-per-second parameter <code>fps</code> is done
 * by this class.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public abstract class AnimatedUIAbstract extends JPanel implements Serializable, Runnable {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected int fps;

    protected Thread thread;

    protected boolean running;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     *  
     * @param fps frames per second
     */
    public AnimatedUIAbstract(int fps) {
        super();
        setFps(fps);
        running = true;
        thread = new Thread(this);
        thread.start(); // background thread for animation
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Background thread for animation.
     */
    public void run() {
        while (running) {
        	this.updateUI();
        	int fps = getFps();
        	int frametime;
        	if (fps > 0) {
        		frametime = 1000 / fps;
        	} else {
        		frametime = 1000; // 1 fps if not available
        	}
        	try {
        		Thread.sleep(frametime);
        	} catch (InterruptedException ie) {
        		//nop
        	}
        }
    }

    public void finalize() {
        running = false;
    }

    /**
     * Get frames-per-second of the animated ui.
     *  
     * @return  frames per second
     */
    public int getFps() {
        return fps;
    }

    /**
     * Set frames-per-second of the animated ui.
     *  
     * @param fps frames per second
     */
    public void setFps(int fps) {
        this.fps = fps;
    }

} // end AnimatedUIAbstract
