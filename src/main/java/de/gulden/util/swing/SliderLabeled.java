/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.swing.SliderLabeled
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 * Note: This utility class is only provided for compiling JJack,
 * it is not part of JJack's CVS tree.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util.swing;

import de.gulden.util.Toolbox;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.event.*;

/**
 *  
 * @author  Jens Gulden
 */
public class SliderLabeled extends JSlider implements ChangeListener {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------

    public static Font FONT_LABEL = new Font("Dialog", Font.PLAIN, 9);


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected JSlider slider;

    protected JLabel label;

    protected JLabel titleLabel;

    protected boolean percentage;

    protected int decimals = -1;

    protected char decimalPointChar = '.';


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public SliderLabeled() {
        this((String)null);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(String title) {
        this(title, 0, 100, 0);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(int min, int max) {
        this(null, min, max, 0);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(String title, int min, int max, int value) {
        this(JSlider.VERTICAL, BorderLayout.SOUTH);
        if (title != null) {
        	setTitle(title);
        }
        getSlider().setMinimum(min);
        getSlider().setMaximum(max);
        getSlider().setValue(value);
        updateUI();
    }

    /**
     * Constructor.
     */
    public SliderLabeled(double min, double max) {
        this(null, min, max);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(String title, int min, int max) {
        this(title, min, max, 0);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(String title, double min, double max) {
        this(title, min, max, 0.0);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(double min, double max, double value) {
        this(null, min, max, value);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(String title, double min, double max, double value) {
        this(title, min, max, value, 2);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(double min, double max, double value, int decimals) {
        this(null, min, max, value, decimals);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(String title, double min, double max, double value, int decimals) {
        this(title, (int)(min*(int)Math.pow(10,decimals)), (int)(max*(int)Math.pow(10,decimals)), (int)(value*(int)Math.pow(10,decimals)));
        setDecimals(decimals);
        updateUI();
    }

    /**
     * Constructor.
     */
    public SliderLabeled(int orientation, Object labelOrientation) {
        this(orientation, labelOrientation, false);
    }

    /**
     * Convenience constructor for percentage value (0%-100%).
     */
    public SliderLabeled(boolean percentage) {
        this((String)null, percentage);
    }

    /**
     * Convenience constructor for percentage value (0%-100%).
     */
    public SliderLabeled(String title, boolean percentage) {
        this(title, JSlider.VERTICAL, BorderLayout.SOUTH, percentage);
    }

    /**
     * Convenience constructor for percentage value (0%-100%).
     */
    public SliderLabeled(int orientation, Object labelOrientation, boolean percentage) {
        this(null, orientation, labelOrientation, percentage);
    }

    /**
     * Convenience constructor for percentage value (0%-100%).
     */
    public SliderLabeled(String title, int orientation, Object labelOrientation, boolean percentage) {
        this(null, null, orientation, labelOrientation);
        if (title != null) {
        	setTitle(title);
        }
        if (percentage) {
        	setPercentage(true);
        }
        updateUI();
    }

    /**
     * Constructor.
     */
    public SliderLabeled(JSlider slider) {
        this(slider, null);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(JSlider slider, boolean percentage) {
        this(slider, null);
        if (percentage) {
        	setPercentage(true);
        }
        updateUI();
    }

    /**
     * Constructor.
     */
    public SliderLabeled(JSlider slider, JLabel label) {
        this(slider, label, BorderLayout.SOUTH);
    }

    /**
     * Constructor.
     */
    public SliderLabeled(JSlider slider, JLabel label, Object labelOrientation) {
        this(slider, label, JSlider.VERTICAL, labelOrientation);
    }

    /**
     * Constructor.
     * This is the main constructor, internally called by all other constructors.
     *  
     * @param slider may be null
     * @param label may be null
     */
    public SliderLabeled(JSlider slider, JLabel label, int orientation, Object labelOrientation) {
        super();
        this.removeAll(); // remove original slider components, use this as a JPanel
        this.setLayout(new BorderLayout());
        if (slider==null) {
        	slider = createSlider(orientation);
        }
        if (label==null) {
        	label = createLabel();
        }
        this.slider = slider;
        this.label = label;
        this.titleLabel = createTitleLabel();

        slider.addChangeListener(this);

        this.add(slider, BorderLayout.CENTER);
        this.add(titleLabel, BorderLayout.NORTH);
        this.add(label, labelOrientation);

        updateUI();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public String getTitle() {
        return getTitleLabel().getText();
    }

    public void setTitle(String title) {
        getTitleLabel().setText(title);
    }

    /**
     * Get the slider's value.
     */
    public int getValue() {
        JSlider slider = getSlider();
        if (slider != null) {
        	return slider.getValue();
        } else {
        	return 0;
        }
    }

    /**
     * Set the slider's value.
     */
    public void setValue(int value) {
        getSlider().setValue(value);
    }

    /**
     * Get the slider's value as double precision type.
     */
    public double getDoubleValue() {
        return (((double)getSlider().getValue()) / decimalPow());
    }

    /**
     * Set the slider's value.
     */
    public void setDoubleValue(double value) {
        getSlider().setValue((int)(value*decimalPow()));
    }

    /**
     * Get the slider's value as string.
     */
    public String getValueString() {
        int value = getSlider().getValue();
        String l = String.valueOf((int)Math.abs(value));
        int dec = getDecimals();
        if (dec > 0) {
        	// insert decimal point into string
        	if (l.length() < dec+1) {
        		l = Toolbox.repeat('0', dec+1-l.length()) + l;
        	}
        	l = l.substring(0, l.length()-dec) + getDecimalPointChar() + l.substring(l.length()-dec);
        }
        if (value < 0) {
        	l = "-" + l;
        }
        return l;
    }

    /**
     * Update label.
     * This overwrites the original JSlider implementation of updateUI().
     */
    public void updateUI() {
        if (getSlider() != null) {
        	String l = getValueString();
        	if (isPercentage()) {
        		l += "%";
        	}
        	getLabel().setText(l);
        }
    }

    /**
     * Implementation of interface ChangeListener.
     */
    public void stateChanged(ChangeEvent e) {
        updateUI();
        // inform own change listeners
        ChangeEvent newE = new ChangeEvent(this);
        ChangeListener[] listeners = this.getChangeListeners();
        for (int i = 0; i < listeners.length; i++) {
        	listeners[i].stateChanged(newE);
        }
    }

    /**
     */
    public char getDecimalPointChar() {
        return decimalPointChar;
    }

    /**
     */
    public int getDecimals() {
        return decimals;
    }

    /**
     */
    public JLabel getLabel() {
        return label;
    }

    /**
     */
    public boolean isPercentage() {
        return percentage;
    }

    /**
     */
    public JSlider getSlider() {
        return slider;
    }

    /**
     */
    public void setDecimalPointChar(char c) {
        decimalPointChar = c;
    }

    /**
     */
    public void setDecimals(int i) {
        decimals = i;
    }

    /**
     */
    public void setLabel(JLabel label) {
        this.label = label;
    }

    /**
     */
    public void setPercentage(boolean b) {
        percentage = b;
    }

    /**
     */
    public void setSlider(JSlider slider) {
        this.slider = slider;
    }

    /**
     */
    public JLabel getTitleLabel() {
        return titleLabel;
    }

    /**
     */
    public void setTitleLabel(JLabel label) {
        titleLabel = label;
    }

    public int getMajorTickSpacing() {
        return slider.getMajorTickSpacing();
    }

    public int getMaximum() {
        return slider.getMaximum();
    }

    public int getMinimum() {
        return slider.getMinimum();
    }

    public int getMinorTickSpacing() {
        return slider.getMinorTickSpacing();
    }

    public BoundedRangeModel getModel() {
        return slider.getModel();
    }

    public int getOrientation() {
        return slider.getOrientation();
    }

    public boolean getPaintLabels() {
        return slider.getPaintLabels();
    }

    public boolean getPaintTicks() {
        return slider.getPaintTicks();
    }

    public boolean getPaintTrack() {
        return slider.getPaintTrack();
    }

    public boolean getSnapToTicks() {
        return slider.getSnapToTicks();
    }

    public String getToolTipText() {
        return slider.getToolTipText();
    }

    public String getToolTipText(MouseEvent event) {
        return slider.getToolTipText(event);
    }

    public void setMajorTickSpacing(int n) {
        slider.setMajorTickSpacing(n);
    }

    public void setMaximum(int maximum) {
        slider.setMaximum(maximum);
    }

    public void setMinimum(int minimum) {
        slider.setMinimum(minimum);
    }

    public void setMinorTickSpacing(int n) {
        slider.setMinorTickSpacing(n);
    }

    public void setModel(BoundedRangeModel newModel) {
        slider.setModel(newModel);
    }

    public void setOrientation(int orientation) {
        slider.setOrientation(orientation);
    }

    public void setPaintLabels(boolean b) {
        slider.setPaintLabels(b);
    }

    public void setPaintTicks(boolean b) {
        slider.setPaintTicks(b);
    }

    public void setPaintTrack(boolean b) {
        slider.setPaintTrack(b);
    }

    public void setSnapToTicks(boolean b) {
        slider.setSnapToTicks(b);
    }

    public void setToolTipText(String text) {
        slider.setToolTipText(text);
    }

    /**
     * Create the default JSlider used by this if no slider is passed from external.
     * May be ovewritten by subclasses to provide different kinds of sliders.
     *  
     * @return  slider
     */
    protected JSlider createSlider(int orientation) {
        return new JSlider(orientation);
    }

    /**
     * Create the default JLabel used by this if no label is passed from external.
     * May be ovewritten by subclasses to provide different kinds of labels.
     *  
     * @return  slider
     */
    protected JLabel createLabel() {
        JLabel label = new JLabel("...", JLabel.CENTER);
        label.setFont(FONT_LABEL);
        return label;
    }

    /**
     * Create the default title label used by this if no label is passed from external.
     * May be ovewritten by subclasses to provide different kinds of labels.
     *  
     * @return  slider
     */
    protected JLabel createTitleLabel() {
        JLabel label = new JLabel("", JLabel.CENTER);
        label.setFont(FONT_LABEL);
        return label;
    }

    protected int decimalPow() {
        int dec = getDecimals();
        if (dec > 0) {
        	return (int)Math.pow(10, dec);
        } else {
        	return 1;
        }
    }

} // end SliderLabeled
