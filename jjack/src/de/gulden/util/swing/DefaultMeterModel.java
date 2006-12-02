/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.swing.DefaultMeterModel
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

/**
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class DefaultMeterModel implements MeterModel {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected double min;

    protected double max;

    protected double minCritical;

    protected double minOverload;


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     */
    public double getMax() {
        return max;
    }

    /**
     */
    public double getMin() {
        return min;
    }

    /**
     */
    public double getMinCritical() {
        return minCritical;
    }

    /**
     */
    public double getMinOverload() {
        return minOverload;
    }

    /**
     */
    public void setMax(double d) {
        max = d;
    }

    /**
     */
    public void setMin(double d) {
        min = d;
    }

    /**
     */
    public void setMinCritical(double d) {
        minCritical = d;
    }

    /**
     */
    public void setMinOverload(double d) {
        minOverload = d;
    }

    /**
     *  
     * @see  de.gulden.util.swing.MeterModel#isNormal(double)
     */
    public boolean isNormal(double value) {
        return value < getMinCritical();
    }

    /**
     *  
     * @see  de.gulden.util.swing.MeterModel#isCritical(double)
     */
    public boolean isCritical(double value) {
        return value >= getMinCritical();
    }

    /**
     *  
     * @see  de.gulden.util.swing.MeterModel#isOverloaded(double)
     */
    public boolean isOverload(double value) {
        return value >= getMinOverload();
    }

} // end DefaultMeterModel
