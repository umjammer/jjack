/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.swing.VUMeter
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 * Note: This utility class is only provided for compiling JJack,
 * it is not part of JJack's CVS tree.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util.swing;

import java.awt.*;
import javax.swing.*;

/**
 *  
 * @author  Jens Gulden
 */
public class VUMeter extends JPanel {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    public static final int STYLE_LED = 1;

    public static final int STYLE_ANALOG = 2;

    public static final Color COLOR_NORMAL = Color.green;

    public static final Color COLOR_CRITICAL = Color.yellow;

    public static final Color COLOR_OVERLOAD = Color.red;


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected int style = STYLE_LED;

    protected int ledCount = 10;

    protected MeterModel model = new DefaultMeterModel();

    protected double value;

    protected LED[] led;

    protected double[] ledValue;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    public VUMeter() {
        this(10);
    }

    public VUMeter(int ledCount) {
        this(ledCount, 0.0, 1.0, 0.75, 0.9);
    }

    public VUMeter(double min, double max, double minCritical, double minOverload) {
        this(10, min, max, minCritical, minOverload);
    }

    public VUMeter(int ledCount, double min, double max, double minCritical, double minOverload) {
        super();
        setLedCount(ledCount);
        setMin(min);
        setMax(max);
        setMinCritical(minCritical);
        setMinOverload(minOverload);
        initLEDs();
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void updateUI() {
        LED[] led = this.led;
        double[] ledValue = this.ledValue;
        if (led!=null && ledValue!=null) {
        	double val = getValue();
        	for (int i = 0; i< led.length; i++) {
        		boolean flashed = (val >= ledValue[i]);
        		led[i].setOn( flashed );
        	}
        	repaint();
        }
    }

    /**
     */
    public MeterModel getModel() {
        return model;
    }

    /**
     */
    public int getStyle() {
        return style;
    }

    /**
     */
    public double getValue() {
        return value;
    }

    /**
     */
    public void setModel(MeterModel model) {
        this.model = model;
    }

    /**
     */
    public void setStyle(int i) {
        style = i;
    }

    /**
     */
    public void setValue(double d) {
        value = d;
    }

    /**
     */
    public double getMax() {
        return model.getMax();
    }

    /**
     */
    public double getMin() {
        return model.getMin();
    }

    /**
     */
    public double getMinCritical() {
        return model.getMinCritical();
    }

    /**
     */
    public double getMinOverload() {
        return model.getMinOverload();
    }

    /**
     */
    public void setMax(double d) {
        model.setMax(d);
    }

    /**
     */
    public void setMin(double d) {
        model.setMin(d);
    }

    /**
     */
    public void setMinCritical(double d) {
        model.setMinCritical(d);
    }

    /**
     */
    public void setMinOverload(double d) {
        model.setMinOverload(d);
    }

    /**
     */
    public int getLedCount() {
        return ledCount;
    }

    /**
     */
    public void setLedCount(int i) {
        ledCount = i;
    }

    protected void initLEDs() {
        this.removeAll();
        int ledCount = getLedCount();
        this.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        MeterModel model = getModel();
        this.led = new LED[ledCount];
        this.ledValue = new double[ledCount];
        for (int i=0; i < ledCount; i++) {
        	double val = model.getMin() + ((model.getMax()-model.getMin())* (ledCount-i) / ledCount);
        	Color ledColor;
        	if (model.isOverload(val)) {
        		ledColor = COLOR_OVERLOAD;
        	} else if (model.isCritical(val)) {
        		ledColor = COLOR_CRITICAL;
        	} else {
        		ledColor = COLOR_NORMAL;
        	}
        	LED l = new LED(ledColor);
        	led[i] = l;
        	ledValue[i] = val;
        	if ( i == ledCount-1) {
        		gc.gridheight = GridBagConstraints.REMAINDER;
        	}
        	this.add(l, gc);
        }
    }

} // end VUMeter
