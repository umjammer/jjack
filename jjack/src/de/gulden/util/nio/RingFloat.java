/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.nio.RingFloat
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 * Note: This utility class is only provided for compiling JJack,
 * it is not part of JJack's CVS tree.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util.nio;

import java.nio.*;

/**
 * Array-based ringbuffer implementation.
 * A ringbuffer does not change in size dynamically, data that
 * first has been put into the buffer willl be overwritten by new data
 * once the maximum ringbuffer capacity has been reached.
 * A program can ensure a minimum buffer size by calling ensureSize(),
 * decreasing of buffer size is not possible.
 *  
 * @author  Jens Gulden
 */
public class RingFloat extends FifoFloat {

    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected float[] buffer;

    protected int pos;


    // ------------------------------------------------------------------------
    // --- constructors                                                     ---
    // ------------------------------------------------------------------------

    public RingFloat() {
        super(); // (many of the superclass's features are replaced with a different implementation here)
        buffer = new float[DEFAULT_ALLOCATION_SIZE];
        pos = 0;
    }

    public RingFloat(int initialCapacity) {
        this();
        ensureCapacity(initialCapacity);
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    public void ensureCapacity(int size) {
        if (buffer.length < size) {
        	float[] newbuffer = new float[size];
        	System.arraycopy(buffer, 0, newbuffer, size-buffer.length, buffer.length);
        	buffer = newbuffer;
        }
    }

    /**
     * Only the remaining buffer content will be used by the fifo-queue.
     */
    public void put(FloatBuffer buf) {
        // untested
        int length = buf.capacity();
        if (length > buffer.length) {
        	throw new BufferOverflowException();
        }
        int l = buffer.length-pos;
        if (length > l) {
        	// copy and wrap around at end
        	buf.get(buffer, pos, l);
        	this.pos = length-l;
        	buf.get(buffer, 0, this.pos);
        } else {
        	// simple copy
        	buf.get(buffer, pos, length);
        	pos += length;
        }
    }

    public void put(float[] f) {
        put(f, 0, f.length);
    }

    public void put(float[] f, int offset, int length) {
        if (length > buffer.length) {
        	throw new BufferOverflowException();
        }
        int l = buffer.length-pos;
        if (length > l) {
        	// copy and wrap around at end
        	System.arraycopy(f, offset, buffer, pos, l);
        	this.pos = length-l;
        	System.arraycopy(f, offset+l, buffer, 0, this.pos);
        } else {
        	// simple copy
        	System.arraycopy(f, offset, buffer, pos, length);
        	pos += length;
        }
    }

    public void put(float f) {
        buffer[pos++] = f;
        if (pos >= buffer.length) {
        	pos = 0;
        }
    }

    public float get(int diff) {
        int i = ((pos - diff) + buffer.length) % buffer.length;
        return buffer[i];
    }

} // end RingFloat
