/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.swing.SpinButtonLabeled
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 * Note: This utility class is only provided for compiling JJack,
 * it is not part of JJack's CVS tree.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util.swing;

import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class SpinButtonLabeled extends SliderLabeled {

    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     */
    public SpinButtonLabeled(int min, int max) {
        super(min, max);
    }

    /**
     */
    public SpinButtonLabeled(int min, int max, int value) {
        super(min, max, value);
    }

    /**
     */
    public SpinButtonLabeled(double min, double max) {
        super(min, max);
    }

    /**
     */
    public SpinButtonLabeled(double min, double max, double value) {
        super(min, max, value);
    }

    /**
     */
    public SpinButtonLabeled(double min, double max, double value, int decimals) {
        super(min, max, value, decimals);
    }

    /**
     */
    public SpinButtonLabeled() {
        super();
    }

    /**
     */
    public SpinButtonLabeled(int orientation, Object labelOrientation) {
        super(orientation, labelOrientation);
    }

    /**
     */
    public SpinButtonLabeled(boolean percentage) {
        super(percentage);
    }

    /**
     */
    public SpinButtonLabeled(int orientation, Object labelOrientation, boolean percentage) {
        super(orientation, labelOrientation, percentage);
    }

    /**
     */
    public SpinButtonLabeled(JSlider slider) {
        super(slider);
    }

    /**
     */
    public SpinButtonLabeled(JSlider slider, boolean percentage) {
        super(slider, percentage);
    }

    /**
     */
    public SpinButtonLabeled(JSlider slider, JLabel label) {
        super(slider, label);
    }

    /**
     */
    public SpinButtonLabeled(JSlider slider, JLabel label, Object labelOrientation) {
        super(slider, label, labelOrientation);
    }

    /**
     */
    public SpinButtonLabeled(String title) {
        super(title);
    }

    /**
     */
    public SpinButtonLabeled(String title, int min, int max, int value) {
        super(title, min, max, value);
    }

    /**
     */
    public SpinButtonLabeled(String title, double min, double max, double value, int decimals) {
        super(title, min, max, value, decimals);
    }

    /**
     */
    public SpinButtonLabeled(String title, boolean percentage) {
        super(title, percentage);
    }

    /**
     */
    public SpinButtonLabeled(String title, int orientation, Object labelOrientation, boolean percentage) {
        super(title, orientation, labelOrientation, percentage);
    }

    /**
     */
    public SpinButtonLabeled(String title, double min, double max) {
        super(title, min, max);
    }

    /**
     */
    public SpinButtonLabeled(String title, double min, double max, double value) {
        super(title, min, max, value);
    }

    /**
     */
    public SpinButtonLabeled(String title, int min, int max) {
        super(title, min, max);
    }

    /**
     */
    public SpinButtonLabeled(JSlider slider, JLabel label, int orientation, Object labelOrientation) {
        super(slider, label, orientation, labelOrientation);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void setSensitivity(float sensitivity) {
        ((SpinButton)slider).setSensitivity(sensitivity);
    }

    public float getSensitivity() {
        return ((SpinButton)slider).getSensitivity();
    }

    /**
     * Create the default JSlider used by this if no slider is passed from external.
     * May be ovewritten by subclasses to provide different kinds of sliders.
     *  
     * @return  slider
     */
    protected JSlider createSlider(int orientation) {
        return new SpinButton();
    }

} // end SpinButtonLabeled
