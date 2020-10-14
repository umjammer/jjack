/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.Toolbox
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 * Note: This utility class is only provided for compiling JJack,
 * it is not part of JJack's CVS tree.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util;

import java.awt.*;
import java.util.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.text.Document;
import javax.swing.JOptionPane;

/**
 * Class Toolbox.
 *  
 * @author  Jens Gulden
 */
public class Toolbox {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * Constant NORTH.
     */
    public static final int NORTH = 1;

    /**
     * Constant EAST.
     */
    public static final int EAST = 2;

    /**
     * Constant SOUTH.
     */
    public static final int SOUTH = 4;

    /**
     * Constant WEST.
     */
    public static final int WEST = 8;

    /**
     * Constant NORTH_EAST.
     */
    public static final int NORTH_EAST = 3;

    /**
     * Constant SOUTH_EAST.
     */
    public static final int SOUTH_EAST = 6;

    /**
     * Constant SOUTH_WEST.
     */
    public static final int SOUTH_WEST = 12;

    /**
     * Constant NORTH_WEST.
     */
    public static final int NORTH_WEST = 9;

    /**
     * Constant CENTER.
     */
    public static final int CENTER = 0;

    /**
     * Constant NL.
     */
    public static final String NL = System.getProperty("line.separator");

    /**
     * Constant primitiveTypeWrappers[][].
     */
    private static final Object[][] primitiveTypeWrappers = {{"boolean","byte","short","int","long","float","double","char"},{java.lang.Boolean.class,java.lang.Byte.class,java.lang.Short.class,java.lang.Integer.class,java.lang.Long.class,java.lang.Float.class,java.lang.Double.class,java.lang.Character.class}};


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    /**
     * Returns the primitive type wrapper class.
     */
    public static Class getPrimitiveTypeWrapperClass(String typename) throws ClassNotFoundException {
        int i=getPrimitiveTypeIndex(typename);
        if (i!=-1) {
            return (Class)primitiveTypeWrappers[1][i];
        } else {
            throw new ClassNotFoundException("'"+typename+"' is not a primitive type");
        }
    }

    /**
     * Returns the primitive type class.
     */
    public static Class getPrimitiveTypeClass(String typename) throws ClassNotFoundException {
        Class wrapper=getPrimitiveTypeWrapperClass(typename);
        try {
            java.lang.reflect.Field field=wrapper.getField("TYPE"); // all wrapper classes have public static field TYPE to represent the primitive Class
            Class primitiveClass=(Class)field.get(null);
            return primitiveClass;
        } catch (Exception e) {
            throw new ClassNotFoundException("wrapper for '"+typename+"' has no TYPE field");
        }
    }

    public static boolean isPrimitiveType(String typename) {
        return (getPrimitiveTypeIndex(typename)!=-1);
    }

    /**
     * Parses the color.
     */
    public static Color parseColor(String s) {
        if ((s.length()==7)&&(s.startsWith("#"))) {
            String rStr=s.substring(1,3);
            String gStr=s.substring(3,5);
            String bStr=s.substring(5,7);
            int r=decimal(rStr);
            int g=decimal(gStr);
            int b=decimal(bStr);
            return new Color(r,g,b);
        } else {
            // try to get from AWT-color-constants
            try {
                java.lang.reflect.Field field=Color.class.getField(s);
                return (Color)field.get(null);
            } catch (Exception e) {
                return null; // not found
            }
        }
    }

    public static String toString(Color color) {
        return "#"+hex(color.getRed(),2)+hex(color.getGreen(),2)+hex(color.getBlue(),2);
    }

    public static String hex(int i) {
        return Integer.toHexString(i);
    }

    public static String hex(int i, int minLength) {
        String s=hex(i);
        int lendiff=minLength-s.length();
        return s+repeat('0',lendiff); // negative values are ok for repeat
    }

    public static int decimal(String hex) {
        return Integer.valueOf(hex,16).intValue();
    }

    public static String repeat(String s, int count) {
        StringBuffer sb=new StringBuffer();
        for (int i=0;i<count;i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public static String repeat(char c, int count) {
        StringBuffer sb=new StringBuffer();
        for (int i=0;i<count;i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static String noNull(String s) {
        if (s!=null) {
            return s;
        } else {
            return "";
        }
    }

    public static String unqualify(String s) {
        int pos=s.lastIndexOf('.');
        if (pos!=-1) {
            return s.substring(pos+1);
        } else {
            return s;
        }
    }

    public static String padRight(String s, String fill, int len) {
        return s+repeat(fill,len-s.length());
    }

    public static String padLeft(String s, String fill, int len) {
        return repeat(fill,len-s.length())+s;
    }

    /**
     * Parses the boolean.
     */
    public static boolean parseBoolean(String s) {
        return s.equalsIgnoreCase("yes")||s.equalsIgnoreCase("y")||s.equalsIgnoreCase("on")||s.equalsIgnoreCase("true");
    }

    public static String capitalize(String s) {
        if (s.length()>0) {
            return s.substring(0,1).toUpperCase()+s.substring(1);
        } else {
            return "";
        }
    }

    public static String unqualifyJavaName(String name) {
        int lastDotPos=name.lastIndexOf('.');
        if (lastDotPos!=-1) {
            return name.substring(lastDotPos+1);
        } else {
            return name;
        }
    }

    public static boolean arrayContains(Object[] array, Object object) {
        for (int i=0;i<array.length;i++) {
            if (object.equals(array[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean empty(String s) {
        return (s==null)||(s.trim().length()==0);
    }

    public static void centerOnScreen(Component component) {
        Dimension screen=component.getToolkit().getScreenSize();
        Dimension comp=component.getSize();
        Point newLocation=new Point((screen.width-comp.width)/2,(screen.height-comp.height)/2);
        component.setLocation(newLocation);
    }

    public static void centerComponent(Component component, Component parent) {
        Dimension p=parent.getSize();
        Dimension comp=component.getSize();
        Point oldPos=parent.getLocation();
        Point newPos=new Point();
        newPos.x=oldPos.x+(p.width-comp.width)/2;
        newPos.y=oldPos.y+(p.height-comp.height)/2;
        component.setLocation(newPos);
    }

    public static Object invokeValueOf(Class clazz, String s) {
        try {
            Class[] signature={String.class};
            java.lang.reflect.Method m=clazz.getMethod("valueOf",signature);
            Object[] params={s};
            Object result=m.invoke(null,params);
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the document text.
     */
    public static String getDocumentText(Document d) {
        try {
            return d.getText(d.getStartPosition().getOffset(),d.getLength());
        } catch (javax.swing.text.BadLocationException ble) {
            return null;
        }
    }

    /**
     * Sets the document text.
     */
    public static void setDocumentText(Document d, String s) {
        try {
            d.remove(d.getStartPosition().getOffset(),d.getLength());
            d.insertString(d.getStartPosition().getOffset(),s,null);
        } catch (javax.swing.text.BadLocationException ble) {
            throw new Error("INTERNAL ERROR: Toolbox.setDocumentText");
        }
    }

    public static Collection filterCollectionOnType(Collection c, Class type) {
        Collection result=new ArrayList();
        for (Iterator it=c.iterator();it.hasNext();) {
            Object o=it.next();
            if (type.isAssignableFrom(o.getClass())) {
                result.add(o);
            }
        }
        return result;
    }

    public static Component findChildComponent(Container c, Class childType) {
        Component[] children=c.getComponents();
        for (int i=0;i<children.length;i++) {
            if (childType==children[i].getClass()) {
                return children[i];
            }
        }
        // if not yet found flatly, recurse search on containers
        for (int i=0;i<children.length;i++) {
            if (children[i] instanceof Container) {
                Component found=findChildComponent((Container)children[i],childType);
                if (found!=null) {
                    return found;
                }
            }
        }
        return null;
    }

    public static void drawString(Graphics g, String s, Point p, int anchor, Insets shift, Color backgroundColor, Insets backgroundBorder) {
        java.awt.FontMetrics fm=g.getFontMetrics();
        int w=fm.stringWidth(s);
        int h=fm.getHeight();
        // AWT draws string anchored at SOUTH_WEST
        if ((anchor&NORTH)!=0) { // north
            p.y+=(h+shift.top);
        } else if ((anchor&SOUTH)==0) { // middle
            p.y+=(h/2);
        } else { // south
            p.y-=shift.bottom;
        }
        if ((anchor&EAST)!=0) { // east
            p.x-=(w+shift.right);
        } else if ((anchor&WEST)==0) { // center
            p.x-=(w/2);
        } else { // west
            p.x+=shift.left;
        }
        if (backgroundColor!=null) {
            if (backgroundBorder==null) {
                backgroundBorder=new Insets(0,0,0,0);
            }
            Color foregroundColor=g.getColor();
            g.setColor(backgroundColor);
            java.awt.Rectangle r=new java.awt.Rectangle();
            r.x=p.x-backgroundBorder.left;
            r.y=p.y-fm.getAscent()-backgroundBorder.top;
            r.width=w+backgroundBorder.left+backgroundBorder.right;
            r.height=h+backgroundBorder.top+backgroundBorder.bottom;
            g.fillRect(r.x,r.y,r.width,r.height);
            g.setColor(foregroundColor);
        }
        g.drawString(s,p.x,p.y);
    }

    public static void drawString(Graphics g, String s, Point p, int anchor, Insets shift) {
        drawString(g,s,p,anchor,shift,null,null);
    }

    public static void drawString(Graphics g, String s, Point p, int anchor) {
        drawString(g,s,p,anchor,new Insets(0,0,0,0));
    }

    public static void fillCircle(Graphics g, Point center, int radius) {
        int x = center.x - radius;
        int y = center.y - radius;
        int wh = radius * 2;
        g.fillOval(x, y, wh, wh);
    }

    /**
     *  
     * @param angle zero means a straight line to the bottom (six o'clock), increasing angle has clockwise effect
     */
    public static void drawLine(Graphics g, Point center, double angle, int radius) {
        drawLine(g, center, angle, 0, radius);
    }

    /**
     */
    public static void drawLine(Graphics g, Point center, double angle, int startRadius, int endRadius) {
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        int x1 = (int) (center.x - sin * startRadius);
        int y1 = (int) (center.y + cos * startRadius);
        int x2 = (int) (center.x - sin * endRadius);
        int y2 = (int) (center.y + cos * endRadius);
        g.drawLine(x1, y1, x2, y2);
    }

    /**
     * Sets the location centered.
     */
    public static void setLocationCentered(Component c, Point p) {
        Dimension size=c.getSize();
        c.setLocation(p.x-size.width/2,p.y-size.height/2);
    }

    /**
     * Show a simple text message box.
     * Think of this as something like JavaScript's alert()-function.
     */
    public static void messageBox(String text) {
        messageBox("Info", text);
    }

    /**
     * Show a simple text message box.
     * Think of this as something like JavaScript's alert()-function.
     */
    public static void messageBox(String title, String text) {
        JOptionPane.showMessageDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static String replace(String s, String search, String repl) {
        int pos=s.indexOf(search);
        if (pos>=0) {
            return s.substring(0,pos)+repl+replace(s.substring(pos+search.length()),search,repl);
        } else {
            return s;
        }
    }

    public static List explode(String s, char seperator) {
        StringTokenizer st=new StringTokenizer(s,String.valueOf(seperator),false);
        List l=new ArrayList();
        while (st.hasMoreTokens()) {
            l.add(st.nextToken());
        }
        return l;
    }

    public static List explode(String s) {
        return explode(s,',');
    }

    public static String implode(Collection c, char separator) {
        StringBuffer sb=new StringBuffer();
        for (Iterator it=c.iterator();it.hasNext();) {
            String s=(String)it.next();
            sb.append(s);
            if (it.hasNext()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static String implode(Collection c) {
        return implode(c,',');
    }

    public static String implode(String[] a, String delim) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < a.length; i++) {
        	sb.append(a[i]);
        	if (i < a.length-1) {
        		sb.append(delim);
        	}
        }
        return sb.toString();
    }

    public static boolean isTrue(String s) {
        if (s!=null) {
        	s=s.trim().toLowerCase();
        	return (s.equalsIgnoreCase("yes")||s.equalsIgnoreCase("true")||s.equalsIgnoreCase("on"));
        } else {
        	return false;
        }
    }

    public static boolean isFalse(String s) {
        if (s!=null) {
        	s=s.trim().toLowerCase();
        	return (s.equalsIgnoreCase("no")||s.equalsIgnoreCase("false")||s.equalsIgnoreCase("off"));
        } else {
        	return false;
        }
    }

    public static boolean isYes(String s) {
        return isTrue(s);
    }

    public static boolean isNo(String s) {
        return isFalse(s);
    }

    public static String replaceCharsWithStrings(String s, char[] c, String[] r) {
        int len=s.length();
        StringBuffer sb=new StringBuffer();
        int[] pos=new int[c.length];
        int firstIndex=-1;
        int first=len;
        int lastPos=0;
        // init first positions of each char
        for (int i=0;i<c.length;i++) {
            int p=s.indexOf(c[i]);
            pos[i]=p;
            if ((p!=-1)&&(p<first)) {
                first=p;
                firstIndex=i;
            }
        }
        do {
            String part=s.substring(lastPos,first);
            sb.append(part);
            if (first<len) { // firstIndex had been found
                sb.append(r[firstIndex]);
                lastPos=first+1;
                if (lastPos<len) { // update next pos of this char
                    pos[firstIndex]=s.indexOf(c[firstIndex],lastPos);
                } else {
                    pos[firstIndex]=-1;
                }
                // find new first
                first=len;
                for (int i=0;i<c.length;i++) {
                    int p=pos[i];
                    if ((p!=-1)&&(p<first)) {
                        first=p;
                        firstIndex=i;
                    }
                }
            } else {
                lastPos=len;
            }
        } while (lastPos<len);
        return sb.toString();
    }

    /**
     * Returns the primitive type index.
     */
    private static int getPrimitiveTypeIndex(String typename) {
        for (int i=0;i<primitiveTypeWrappers[0].length;i++) {
            if (primitiveTypeWrappers[0][i].equals(typename)) {
                return i;
            }
        }
        return -1;
    }

} // end Toolbox
