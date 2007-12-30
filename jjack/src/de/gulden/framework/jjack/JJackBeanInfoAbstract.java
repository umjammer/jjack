/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackBeanInfoAbstract
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.awt.Image;
import java.beans.*;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Abstract base class for BeanInfo classes describing JJack clients as JavaBeans.
 * If a JJack client is a subclass of JJackMonitor or JJackClient, it can be used as a JavaBean.
 * This class makes it easy to create the corresponding BeanInfo class.
 *  
 * @author  Jens Gulden
 */
public abstract class JJackBeanInfoAbstract extends SimpleBeanInfo {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * Prefix path to internal JJack icon images.
     */
    public static final String IMAGE_PREFIX = "de/gulden/application/jjack/clients/res/icons/";

    /**
     * Suffix of internal JJack icon images.
     */
    public static final String IMAGE_SUFFIX = ".png";


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * Bean-class that gets described by this BeanInfo-class.
     */
    protected Class thisClass;

    /**
     * Number of <code>PropertyDescriptor</code>s.
     */
    protected int propertyDescriptorsCount;

    /**
     * Number of <code>MethodDescriptor</code>s.
     */
    protected int methodDescriptorsCount;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     *  
     * @param cl Bean-class that gets described by this BeanInfo-class.
     */
    protected JJackBeanInfoAbstract(Class cl) {
        this(cl, 0, 0);
    }

    /**
     * Constructor.
     *  
     * @param cl Bean-class that gets described by this BeanInfo-class.
     * @param extraPropertyDescriptorsCount number of PropertyDescriptor</code>s that are additionally handled by subclass
     * @param extraMethodDescriptorsCount number of MethodDescriptor</code>s that are additionally handled by subclass
     */
    protected JJackBeanInfoAbstract(Class cl, int extraPropertyDescriptorsCount, int extraMethodDescriptorsCount) {
        super();
        this.thisClass = cl;
        this.propertyDescriptorsCount = extraPropertyDescriptorsCount + 7;
        this.methodDescriptorsCount = extraMethodDescriptorsCount + 1;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Gets the beans <code>BeanDescriptor</code>.
     *  
     * @return  A BeanDescriptor providing overall information about
     * the bean, such as its displayName, its customizer, etc.
     */
    public BeanDescriptor getBeanDescriptor() {
        BeanDescriptor bd = new BeanDescriptor(thisClass, null);
        return bd;
    }

    /**
     * Gets the beans <code>PropertyDescriptor</code>s.
     *  
     * @return  An array of PropertyDescriptors describing the editable
     * properties supported by this bean.
     */
    public PropertyDescriptor[] getPropertyDescriptors() {
        PropertyDescriptor[] p = new PropertyDescriptor[propertyDescriptorsCount];
        try {
        	p[0] = new PropertyDescriptor("name", thisClass);
        	p[propertyDescriptorsCount-1] = new PropertyDescriptor("size", thisClass);
        	p[propertyDescriptorsCount-2] = new PropertyDescriptor("border", thisClass);
        	p[propertyDescriptorsCount-3] = new PropertyDescriptor("background", thisClass);
        	p[propertyDescriptorsCount-4] = new PropertyDescriptor("visible", thisClass);
        	// workaround properties to allow connecting beans to each other in BeanBuilder:
        	p[propertyDescriptorsCount-5] = new PropertyDescriptor("monitor", thisClass, null, "addMonitor");
        	p[propertyDescriptorsCount-5].setHidden(true);
        	p[propertyDescriptorsCount-6] = new PropertyDescriptor("chained", thisClass, null, "setChained");
        	p[propertyDescriptorsCount-6].setHidden(true);
        } catch (IntrospectionException ie) {
        	JJackMonitor.exc(ie);
        }
        return p;
    }

    /**
     * Gets the beans <code>EventSetDescriptor</code>s.
     *  
     * @return  An array of EventSetDescriptors describing the kinds of events fired by this bean.
     */
    public EventSetDescriptor[] getEventSetDescriptors() {
        EventSetDescriptor[] e = new EventSetDescriptor[3];
        	  try {
        		  e[0] = new EventSetDescriptor(
        		  	thisClass,
        			"monitor",
        		   	JJackAudioConsumer.class,
        			new String[] {"process"},
        			"addMonitor",
        			"removeMonitor" );
        		e[1] = new EventSetDescriptor(
        		  thisClass,
        		  "chained",
        		  JJackAudioProducer.class,
        		  new String[] {"process"},
        		  "setChained",
        		  "removeChained" );
        		e[1].setUnicast(true);
        		e[2] = new EventSetDescriptor(
        		  thisClass,
        		  "processListener",
        		  JJackAudioProcessListener.class,
        		  new String[] {"beforeProcess", "afterProcess"},
        		  "addAudioProcessListener",
        		  "removeAudioProcessListener" );
        	  }
        	  catch( IntrospectionException ie) {
        		JJackMonitor.exc(ie);
        	  }
        	  return e;
    }

    /**
     * A bean may have a "default" event that is the event that will mostly
     * commonly be used by humans when using the bean.
     *  
     * @return  Index of default event in the EventSetDescriptor array returned by getEventSetDescriptors.
     */
    public int getDefaultEventIndex() {
        return 0;
    }

    /**
     * Gets the beans <code>MethodDescriptor</code>s.
     *  
     * @return  An array of MethodDescriptors describing the externally visible methods supported by this bean.
     */
    public MethodDescriptor[] getMethodDescriptors() {
        MethodDescriptor[] m = new MethodDescriptor[methodDescriptorsCount];
        try {
        	m[methodDescriptorsCount-1] = new MethodDescriptor(thisClass.getMethod("process", new Class[] {JJackAudioEvent.class}));
        } catch (Exception e) {
        	exc(e);
        }
        return m;
    }

    /**
     * This method returns an image object that can be used to
     * represent the bean in toolboxes, toolbars, etc.
     *  
     * @param kind The kind of icon requested. This should be
     * one of the constant values ICON_COLOR_16x16, ICON_COLOR_32x32,
     * ICON_MONO_16x16, or ICON_MONO_32x32.
     * @return  An image object representing the requested icon. May
     * return null if no suitable icon is available.
     */
    public Image getIcon(int kind) {
        String imgRes = IMAGE_PREFIX + de.gulden.util.Toolbox.unqualify(thisClass.getName()).toLowerCase() + IMAGE_SUFFIX;
        URL res = this.getClass().getClassLoader().getResource(imgRes);
        if (res != null) {
        	ImageIcon icon = new ImageIcon(res);
        	return icon.getImage();
        } else {
        	return null;
        }
    }

    /**
     * Outputs an <code>Exception</code>'s type and message.
     * Program execution is not interrupted.
     *  
     * @param e the exception
     */
    protected void exc(Exception e) {
        JJackMonitor.exc(e);
    }

} // end JJackBeanInfoAbstract
