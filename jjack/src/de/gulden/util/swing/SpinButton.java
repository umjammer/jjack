/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.swing.SpinButton
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
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  
 * @author  Jens Gulden
 */
public class SpinButton extends JSlider implements ChangeListener, MouseListener, MouseMotionListener {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------

    public static int DEFAULT_RADIUS = 30;


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected boolean filled = true;

    protected Color borderColor = Color.gray;

    protected Color tickColor = Color.lightGray;

    protected Color fillColor = Color.black;

    protected int borderSize = 2;

    protected int tickSize = 2;

    protected float sensitivity = 1.0f;

    protected double startAngle = Math.PI*2*30/360;

    protected double endAngle = Math.PI*2*330/360;

    private transient int pressedY = -1;

    private transient int pressedValue;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     */
    public SpinButton() {
        this(0, 100);
    }

    /**
     */
    public SpinButton(int min, int max) {
        this(min, max, 0);
    }

    /**
     */
    public SpinButton(int min, int max, int value) {
        this(new DefaultBoundedRangeModel(value, 0, min, max));
    }

    /**
     */
    public SpinButton(BoundedRangeModel sliderModel) {
        super();
        setOpaque(false);
        setPreferredSize(new Dimension(DEFAULT_RADIUS, DEFAULT_RADIUS));
        setModel(sliderModel);
        this.addChangeListener(this);
        MouseListener[] mm = this.getMouseListeners();
        for (int i=0; i<mm.length; i++) {
        	this.removeMouseListener(mm[i]);
        }
        this.addMouseListener(this);
        MouseMotionListener[] mmm = this.getMouseMotionListeners();
        for (int i=0; i<mm.length; i++) {
        	this.removeMouseMotionListener(mmm[i]);
        }
        this.addMouseMotionListener(this);

    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void paint(Graphics g) {
        g.setColor(this.getBorderColor());
        Point center = getCenter();
        int r = getRadius();
        Toolbox.fillCircle(g, center, r);
        if (this.isFilled()) {
        	g.setColor(this.getFillColor());
        } else {
        	g.setColor(this.getBackground());
        }
        Toolbox.fillCircle(g, center, r - this.getBorderSize());
        double startAngle = getStartAngle();
        double endAngle = getEndAngle();
        double zeroAngle = valueToAngle(0);
        double valueAngle = valueToAngle(getValue());
        int radius = getRadius();
        int tickRadiusEnd = radius - getBorderSize();
        int tickRadiusStart = tickRadiusEnd - getTickSize();
        int valueRadius = tickRadiusEnd - 1;
        g.setColor(getTickColor());
        Toolbox.drawLine(g, center, startAngle, tickRadiusStart, tickRadiusEnd);
        Toolbox.drawLine(g, center, endAngle, tickRadiusStart, tickRadiusEnd);
        Toolbox.drawLine(g, center, zeroAngle, tickRadiusStart, tickRadiusEnd);
        g.setColor(this.getForeground());
        Toolbox.drawLine(g, center, valueAngle, valueRadius);
    }

    public void mouseDragged(MouseEvent e) {
        if ( pressedY !=-1 ) {
        	int draggedY = e.getY();
        	int delta = pressedY - draggedY;
        	if (!e.isMetaDown()) {
        		delta *= getSensitivity();
        	}
        	setValue(pressedValue + delta);
        }
    }

    public void mouseMoved(MouseEvent e) {
        // nop
    }

    public void mouseClicked(MouseEvent e) {
        // nop
    }

    public void mouseEntered(MouseEvent e) {
        // nop
    }

    public void mouseExited(MouseEvent e) {
        // nop
    }

    public void mousePressed(MouseEvent e) {
        pressedValue = getValue();
        pressedY = e.getY();
    }

    public void mouseReleased(MouseEvent e) {
        pressedY = -1;
    }

    /**
     */
    public float getSensitivity() {
        return sensitivity;
    }

    /**
     */
    public void setSensitivity(float f) {
        sensitivity = f;
    }

    /**
     * Self-listener.
     *  
     * @see  javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    /**
     */
    public double getEndAngle() {
        return endAngle;
    }

    /**
     */
    public double getStartAngle() {
        return startAngle;
    }

    /**
     */
    public void setEndAngle(double d) {
        endAngle = d;
    }

    /**
     */
    public void setStartAngle(double d) {
        startAngle = d;
    }

    public Point getCenter() {
        Dimension d = getSize();
        Point p = new Point(d.width / 2, d.height / 2);
        return p;
    }

    public int getRadius() {
        Dimension d = getSize();
        return Math.min(d.width / 2, d.height / 2);
    }

    /**
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     */
    public boolean isFilled() {
        return filled;
    }

    /**
     */
    public void setBorderColor(Color color) {
        borderColor = color;
    }

    /**
     */
    public void setFillColor(Color color) {
        fillColor = color;
    }

    /**
     */
    public void setFilled(boolean b) {
        filled = b;
    }

    /**
     */
    public int getBorderSize() {
        return borderSize;
    }

    /**
     */
    public void setBorderSize(int i) {
        borderSize = i;
    }

    /**
     */
    public int getTickSize() {
        return tickSize;
    }

    /**
     */
    public void setTickSize(int i) {
        tickSize = i;
    }

    /**
     */
    public Color getTickColor() {
        return tickColor;
    }

    /**
     */
    public void setTickColor(Color color) {
        tickColor = color;
    }

    protected double valueToAngle(int val) {
        double start = getStartAngle();
        double end = getEndAngle();
        int min = getMinimum();
        int max = getMaximum();
        double angle = start + (end-start)*(((double)(val-min))/(max-min));
        return angle;
    }

} // end SpinButton
