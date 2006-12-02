/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackAudioEvent
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
import java.util.*;

/**
 * Event class to propagate audio data from one audio processor to another.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class JJackAudioEvent extends EventObject {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    /**
     * UNIX timestamp of event occurrence
     */
    protected long timestamp;

    /**
     * Input buffers of this event.
     */
    protected FloatBuffer[] inputBuffer;

    /**
     * Output buffers of this event.
     */
    protected FloatBuffer[] outputBuffer;

    /**
     * Number of input port to use for mono processors.
     */
    protected int monoInputPort;

    /**
     * Number of output port to use for mono processors.
     */
    protected int monoOutputPort;

    /**
     * Backup stack for output buffers during processing.
     * Package-private, accessed by JJackMonitor only.
     */
    Stack stack = new Stack();


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Constructor. Creates a new JJackAudioEvent instance with the given parameters.
     *  
     * @param timestamp UNIX timestamp of event occurrence
     * @param client source client issuing the event
     * @param inputBuffer array of input buffers to deliver with this event
     * @param outputBuffer array of output buffers to deliver with this event
     */
    public JJackAudioEvent(long timestamp, Object client, FloatBuffer[] inputBuffer, FloatBuffer[] outputBuffer) {
        super(client);
        this.timestamp = timestamp;
        this.inputBuffer = inputBuffer;
        this.outputBuffer = outputBuffer;
        this.monoInputPort = 0;
        this.monoOutputPort = 0;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Returns the UNIX timestamp marking the time of event occurrence.
     *  
     * @return  the UNIX timestamp value
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the mono input buffer.
     *  
     * @return  input buffer of the mono input port.
     * @see  #getMonoInputPort()
     */
    public FloatBuffer getInput() {
        return getInput(getMonoInputPort());
    }

    /**
     * Sets the mono input buffer.
     *  
     * @param buf input buffer
     * @see  #setMonoInputPort(int)
     */
    public void setInput(FloatBuffer buf) {
        setInput(getMonoInputPort(), buf);
    }

    /**
     * Returns the mono output buffer.
     *  
     * @return  output buffer of the mono output port.
     * @see  #getMonoOutputPort()
     */
    public FloatBuffer getOutput() {
        return getOutput(getMonoOutputPort());
    }

    /**
     * Sets the mono output buffer.
     *  
     * @param buf output buffer
     * @see  #setMonoOutputPort(int)
     */
    public void setOutput(FloatBuffer buf) {
        setOutput(getMonoOutputPort(), buf);
    }

    /**
     * Returns the input buffer of the specified channel.
     *  
     * @param chan channel number
     * @return  input buffer
     */
    public FloatBuffer getInput(int chan) {
        return inputBuffer[chan];
    }

    /**
     * Sets the input buffer of the specified channel.
     *  
     * @param chan channel number
     * @param buf input buffer
     */
    public void setInput(int chan, FloatBuffer buf) {
        inputBuffer[chan] = buf;
    }

    /**
     * Returns the output buffer of the specified channel.
     *  
     * @param chan channel number
     * @return  output buffer
     */
    public FloatBuffer getOutput(int chan) {
        return outputBuffer[chan];
    }

    /**
     * Sets the output buffer of the specified channel.
     *  
     * @param chan channel number
     * @param buf output buffer
     */
    public void setOutput(int chan, FloatBuffer buf) {
        outputBuffer[chan] = buf;
    }

    /**
     * Returns the number of channels with an input port.
     *  
     * @return  number of channels with an input port
     */
    public int countInputPorts() {
        return inputBuffer.length;
    }

    /**
     * Returns the number of channels with an output port.
     *  
     * @return  number of channels with an output port
     */
    public int countOutputPorts() {
        return outputBuffer.length;
    }

    /**
     * Returns the number of channels that have both an input and an output channel.
     * Usually, the number of input and output channels are equal, in that case
     * <code>countInputPorts() == countOutputPorts() == countChannels()</code>.
     * If the number of input and output ports differ, the minimum of both determines the
     * number of channels.
     *  
     * @return  number of channels
     */
    public int countChannels() {
        return Math.min(countInputPorts(), countOutputPorts());
    }

    /**
     * Returns all input buffers.
     *  
     * @return  array of all input buffers
     */
    public FloatBuffer[] getInputs() {
        return inputBuffer;
    }

    /**
     * Returns all output buffers.
     *  
     * @return  array of all output buffers
     */
    public FloatBuffer[] getOutputs() {
        return outputBuffer;
    }

    /**
     * Returns the index number of the input port used for mono access.
     *  
     * @return  input port index number
     */
    public int getMonoInputPort() {
        return monoInputPort;
    }

    /**
     * Returns the index number of the output port used for mono access.
     *  
     * @return  output port index number
     */
    public int getMonoOutputPort() {
        return monoOutputPort;
    }

    /**
     * Sets the index number of the input port used for mono access.
     */
    public void setMonoInputPort(int port) {
        monoInputPort = port;
    }

    /**
     * Sets the index number of the output port used for mono access.
     */
    public void setMonoOutputPort(int port) {
        monoOutputPort = port;
    }

    /**
     * Returns all channels. The number of elements in the <code>Collection</code>
     * returned is equals to <code>countChannels()</code>.
     *  
     * @return  <code>Collection</code> of <code>JJackAudioChannel</code>
     * @see  #countChannels()
     */
    public Collection getChannels() {
        ArrayList list = new ArrayList();
        for (int i=0; i<countChannels(); i++) {
        	list.add(new ChannelImpl(i, getInput(i), getOutput(i)));
        }
        return list;
    }


    // ************************************************************************
    // *** inner classes                                                    ***
    // ************************************************************************

    /**
     * Inner class implementing interface <code>JJackAudioChannel</code>.
     *  
     * @author  Jens Gulden
     * @version  0.2
     */
    protected class ChannelImpl implements JJackAudioChannel, JJackConstants {

        // ------------------------------------------------------------------------
        // --- fields                                                           ---
        // ------------------------------------------------------------------------

        /**
         * Index number of this channel.
         */
        protected int index;

        /**
         * Input buffer.
         */
        protected JJackAudioPort in;

        /**
         * Output buffer.
         */
        protected JJackAudioPort out;


        // ------------------------------------------------------------------------
        // --- constructor                                                      ---
        // ------------------------------------------------------------------------

        /**
         * Constructor. Create a new instance of <code>ChannelImpl</code> with the given parameters.
         *  
         * @param index index number of this channel
         * @param in input buffer
         * @param out output buffer
         */
        ChannelImpl(int index, FloatBuffer in, FloatBuffer out) {
            this.index = index;
            this.in = new PortImpl(INPUT, in);
            this.out = new PortImpl(OUTPUT, out);
        }


        // ------------------------------------------------------------------------
        // --- methods                                                          ---
        // ------------------------------------------------------------------------

        /**
         * Returns the input or output port of this channel.
         *  
         * @param port either constant <code>INPUT</code> or <code>OUTPUT</code>
         * @return  the input or output port, as requested
         */
        public JJackAudioPort getPort(int port) {
            switch (port) {
            	case INPUT: return in;
            	case OUTPUT: return out;
            	default: return null;
            }
        }

        /**
         * Returns the input or output buffer of this channel.
         * This is a convenience method for <code>getPort(port).getBuffer()</code>.
         *  
         * @param port either constant <code>INPUT</code> or <code>OUTPUT</code>
         * @return  the audio data buffer
         */
        public FloatBuffer getPortBuffer(int port) {
            return getPort(port).getBuffer();
        }

        /**
         * Returns the index number of this channel.
         * In stereo configurations (default), the returned value is either constant <code>LEFT</code>(=0) or <code>RIGHT</code>(=1).
         *  
         * @return  the index number of the channel
         */
        public int getIndex() {
            return index;
        }

    } // end ChannelImpl

    /**
     * Inner class implementing interface <code>JJackAudioPort</code>.
     *  
     * @author  Jens Gulden
     * @version  0.2
     */
    protected class PortImpl implements JJackAudioPort, JJackConstants {

        // ------------------------------------------------------------------------
        // --- fields                                                           ---
        // ------------------------------------------------------------------------

        /**
         * Port mode, either constant <code>INPUT</code> or <code>OUTPUT</code>.
         */
        protected int port;

        /**
         * Buffer associated with this port.
         */
        protected FloatBuffer buf;


        // ------------------------------------------------------------------------
        // --- constructor                                                      ---
        // ------------------------------------------------------------------------

        /**
         * Constructor. Create a new instance of <code>PortImpl</code> with the given parameters.
         *  
         * @param port the port mode, either constant <code>INPUT</code> or <code>OUTPUT</code>
         * @param buf the buffer associated with this port
         */
        PortImpl(int port, FloatBuffer buf) {
            this.port = port;
            this.buf = buf;
        }


        // ------------------------------------------------------------------------
        // --- method                                                           ---
        // ------------------------------------------------------------------------

        /**
         * Returns the <code>FloatBuffer</code> that holds the audio data associated with this port.
         *  
         * @return  <code>FloatBuffer</code>, either read-only (if this is an input port), or write-only (if this is an output port)
         */
        public FloatBuffer getBuffer() {
            return buf;
        }

    } // end PortImpl

} // end JJackAudioEvent
