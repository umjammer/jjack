/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackMonitor
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.nio.FloatBuffer;
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.*;

/**
 * Abstract base class for JJack clients that listen to input
 * but do not generate output.
 * The feature of generating output is added by the subclass JJackClient.
 * Classes derived from this one can also be used as visible JavaBeans.
 *  
 * @author  Jens Gulden
 * @version  0.2
 * @see  JJackClient
 * @see  JJackBeanInfoAbstract
 */
public abstract class JJackMonitor extends JPanel implements JJackAudioProcessorMonitorable, JJackAudioConsumer, JJackAudioProcessListener, JJackConstants {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * The audio processor's name.
     */
    protected String name;

    /**
     * The GUI component associated with this audio processor.
     */
    protected JComponent gui;

    /**
     * Flag marking active mode.
     * In active mode, the output of this processor is being swapped to be the
     * input of any chained and monitoring processor.
     * In inactive mode, inputs to this processor are simply passed on to the next
     * processors.
     */
    protected boolean active;

    /**
     * Chained audio processor.
     * <code>null</code> if this is the last processor in the chain.
     */
    protected JJackAudioProducer chained = null;

    /**
     * Monitor-processors connected to this processor.
     */
    protected ArrayList monitors = new ArrayList();

    /**
     * <code>JJackAudioProcessListener</code>s connected to this processor.
     */
    protected ArrayList listeners = new ArrayList();


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     * Creates a new instance named as the unqualified classname
     * and initially set to <i>inactive</i> processing mode.
     */
    public JJackMonitor() {
        this(false);
    }

    /**
     * Constructor.
     * Creates a new instance, initially set to <i>inactive</i> processing mode.
     *  
     * @param name the processor's name
     */
    public JJackMonitor(String name) {
        this(name, false);
    }

    /**
     * Constructor.
     * Creates a new instance named as the unqualified classname.
     *  
     * @param active sets whether to run in active or inactive mode.
     * @see  #active
     */
    public JJackMonitor(boolean active) {
        this(null, active);
    }

    /**
     * Constructor.
     *  
     * @param name the processor's name
     * @param active sets whether to run in active or inactive mode.
     * @see  #active
     */
    public JJackMonitor(String name, boolean active) {
        super();
        this.active = active;
        if (name==null) {
        	name = this.getClass().getName();
        	int p = name.lastIndexOf('.');
        	if (p!=-1) {
        		name = name.substring(p+1);
        	}
        }
        setName(name);
        this.initUI();
        this.addAudioProcessListener(this); // register self as listener
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the audio processor's name.
     *  
     * @return  the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the audio processor's name.
     *  
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the sample rate with which the JACK daemon is running.
     *  
     * @return  the sample rate in samples per second
     */
    public int getSampleRate() {
        return JJackSystem.getSampleRate();
    }

    /**
     * Sets the next audio processor in chain.
     * The chained processor will get the output of this processor as its own input.
     *  
     * @param p the next audio processor in chain
     */
    public void setChained(JJackAudioProducer p) {
        this.chained = (JJackAudioProducer)p;
    }

    /**
     * Removes <code>p</code> as the next audio processor in chain,
     * if it is the currently chained processor.
     *  
     * @param p the current audio processor in chain
     */
    public void removeChained(JJackAudioProducer p) {
        if (p==getChained()) {
        	setChained(null);
        }
    }

    /**
     * Returns the next audio processor in chain.
     *  
     * @return  the next audio processor in chain, <code>null</code> if no processor is chained to <code>this</code>
     */
    public JJackAudioProducer getChained() {
        return this.chained;
    }

    /**
     * Adds a monitor processor after this processor.
     * The monitor processor will get the output of this processor as its own input.
     */
    public void addMonitor(JJackAudioConsumer p) {
        if (!monitors.contains(p)) {
            monitors.add(p);
        }
    }

    /**
     * Removes a monitor processor from this processor.
     */
    public void removeMonitor(JJackAudioConsumer p) {
        this.monitors.remove(p);
    }

    /**
     * Returns all monitor processors currently connected after this processor.
     *  
     * @return  Collection of JJackAudioConsumer
     */
    public Collection getMonitors() {
        return Collections.unmodifiableCollection(this.monitors);
    }

    /**
     * Adds a <code>JJackAudioProcessListener</code> to this audio processor.
     *  
     * @param l the <code>JJackAudioProcessListener</code> to add
     */
    public void addAudioProcessListener(JJackAudioProcessListener l) {
        if (!listeners.contains(l)) {
        	listeners.add(l);
        }
    }

    /**
     * Removes a <code>JJackAudioProcessListener</code> from this audio processor.
     *  
     * @param l the <code>JJackAudioProcessListener</code> to remove
     */
    public void removeAudioProcessListener(JJackAudioProcessListener l) {
        listeners.remove(l);
    }

    /**
     * Returns all <code>JJackAudioProcessListener</code>s.
     *  
     * @return  Collection of <code>JJackAudioProcessListener</code>
     */
    public Collection getAudioProcessListeners() {
        return Collections.unmodifiableCollection(this.listeners);
    }

    /**
     * Event handler method called before the supervised audio processor
     * performs its <code>process()</code>-method.
     *  
     * @param e audio event that is going to be processed by the supervised audio processor
     */
    public void beforeProcess(JJackAudioEvent e) {
        Collection monitors = getMonitors();
        JJackAudioProcessor chained = getChained();
        if ((chained != null) && (this.active)) {
        	FloatBuffer[] outs = e.getOutputs();
        	FloatBuffer[] outsBack = new FloatBuffer[outs.length];
        	for (int i=0; i < outs.length; i++) {
        		outsBack[i] = outs[i];
        		outs[i] = FloatBuffer.allocate(outs[i].capacity());
        	}
        	e.stack.push(outsBack);
        }
    }

    /**
     * Event handler method called after the supervised audio processor
     * has performed its <code>process()</code>-method.
     *  
     * @param e audio event that has been processed by the supervised audio processor
     */
    public void afterProcess(JJackAudioEvent e) {
        JJackAudioProcessor chained = getChained();
        Collection monitors = getMonitors();
        if (!monitors.isEmpty()) {
        	FloatBuffer newIns[];
        	FloatBuffer[] outs = e.getOutputs();
        	if (this.active) {
        		// new event, output of this as input of next
        		newIns = new FloatBuffer[outs.length];
        		System.arraycopy(outs, 0, newIns, 0, outs.length);
        	} else {
        		FloatBuffer[] ins = e.getInputs();
        		newIns = new FloatBuffer[ins.length];
        		System.arraycopy(ins, 0, newIns, 0, ins.length);
        	}
        	// optimization? do we really need to allocate (dummy-)output buffers for monitor-clients? (they souldn't output anything)
        	FloatBuffer newOuts[] = new FloatBuffer[outs.length];
        	int cap = outs[0].capacity();
        	for (int i=0; i<newOuts.length; i++) {
        		newOuts[i] = FloatBuffer.allocate(cap);
        	}
        	for (Iterator it = monitors.iterator(); it.hasNext(); ) {
        		JJackAudioProcessor p=(JJackAudioProcessor)it.next();
        		for (int i=0; i<newIns.length; i++) {
        			newIns[i].rewind();
        		}
        		for (int i=0; i<newOuts.length; i++) {
        			newOuts[i].rewind();
        		}
        		JJackAudioEvent me = new JJackAudioEvent(e.getTimestamp(), this, newIns, newOuts);
        		JJackSystem.process(p, me);
        	}
        }
        if (chained != null) {
        	if (this.active) {
        		// flip output of this to input of next
        		FloatBuffer[] ins = e.getInputs();
        		FloatBuffer[] outs = e.getOutputs();
        		FloatBuffer[] outsBack = (FloatBuffer[])e.stack.pop();
        		int ports = e.countChannels();
        		System.arraycopy(outs, 0, ins, 0, ports);
        		System.arraycopy(outsBack, 0, outs, 0, ports);
        	}
        	JJackSystem.process(chained, e);
        }
    }

    /**
     * Synchronize user interface with parameter values.
     */
    public void updateUI() {
        super.updateUI();
        if (gui != null) {
        	gui.updateUI();
        	gui.validate();
        }
    }

    /**
     * Returns a short info text about this audio processor.
     *  
     * @return  info text, or <code>null</code> if no info is available
     */
    public String getInfo() {
        return null;
    }

    /**
     * Process multiple samples from input buffer to output buffer.
     * This is regularly called by the JACK daemon.
     *  
     * @param e event object with references to input buffer and output buffer.
     */
    public abstract void process(JJackAudioEvent e);

    /**
     * Initializes the user interface.
     *  
     * @see  #createUI()
     */
    protected void initUI() {
        setLayout(new BorderLayout());
        this.gui = createUI();
        if (this.gui!=null) {
        	this.add(this.gui);
        } else {
        	this.add(new JLabel(this.getName())); // default: label with name
        }
    }

    /**
     * Creates the user interface.
     *  
     * @return  visible component with the user interface, or <code>null</code> if this is an invisible audio processor
     */
    protected JComponent createUI() {
        return null; // invisible by default, to be overwritten by subclass
    }


    // ------------------------------------------------------------------------
    // --- static method                                                    ---
    // ------------------------------------------------------------------------

    /**
     * Outputs an <code>Exception</code>'s type and message.
     * Program execution is not interrupted.
     *  
     * @param e the exception
     */
    static void exc(Exception e) {
        System.out.println(e.getClass().getName()+": "+e.getMessage());
    }

} // end JJackMonitor
