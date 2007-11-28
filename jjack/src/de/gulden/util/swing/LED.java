/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.swing.LED
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
import javax.swing.JPanel;

/**
 *  
 * @author  Jens Gulden
 */
public class LED extends JPanel {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected boolean on = false;

    protected Color color = Color.red;

    protected Color colorOff = Color.red.darker().darker();

    protected Color borderColor = Color.darkGray;

    protected int borderSize = 1;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    public LED() {
        super();
        setPreferredSize(new Dimension(20, 12));
        setSize(new Dimension(20, 12));
    }

    public LED(Dimension size, Color color) {
        this();
        this.setSize(size);
        this.setColor(color);
    }

    public LED(Color color) {
        this();
        this.setColor(color);
    }

    public LED(boolean on) {
        this();
        this.setOn(on);
    }

    public LED(Dimension size, Color color, boolean on) {
        this();
        this.setSize(size);
        this.setColor(color);
        this.setOn(on);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void paint(Graphics g) {
        Dimension size = this.getSize();
        boolean on = this.isOn();
        int bs = this.getBorderSize();
        // draw light
        Color col = on ? this.color : this.colorOff;
        g.setColor(col);
        g.fillRect(bs, bs, size.width - bs*2, size.height - bs*2 );
        // draw reflection
        int rsX = size.width / 10 + bs;
        int rsY = size.height / 10 + bs;
        g.setColor( on ? Color.white : Color.lightGray);
        g.drawLine(rsX+1, rsY, rsX+1, rsY+1);
        g.drawLine(rsX, rsY+1, rsX, rsY+1); // (single dot)
        // draw border
        g.setColor(this.getBorderColor());
        g.drawRect(0, 0, size.width-1, size.height-1);
    }

    /**
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     */
    public Color getColor() {
        return color;
    }

    /**
     */
    public boolean isOn() {
        return on;
    }

    /**
     */
    public void setBorderColor(Color color) {
        borderColor = color;
    }

    /**
     */
    public void setColor(Color color) {
        this.color = color;
        this.colorOff = color.darker().darker();
    }

    /**
     */
    public void setOn(boolean b) {
        on = b;
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

} // end LED
