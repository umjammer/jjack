/*
 * Project: Gulden's Utilities [here as part of JJack]
 * Class:   de.gulden.util.nio.FifoFloat
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 * Note: This utility class is only provided for compiling JJack,
 * it is not part of JJack's CVS tree.
 *
 * Author:  Jens Gulden
 */

package de.gulden.util.nio;

import java.nio.BufferUnderflowException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Fifo for float values.
 * This class is synchronized.
 *  
 * @author  Jens Gulden
 */
public class FifoFloat {

    // ------------------------------------------------------------------------
    // --- static field                                                     ---
    // ------------------------------------------------------------------------

    public static int DEFAULT_ALLOCATION_SIZE = 2048;


    // ------------------------------------------------------------------------
    // --- fields                                                           ---
    // ------------------------------------------------------------------------

    protected ArrayList fifo;

    protected int avail;

    protected FloatBuffer appendable;

    protected int allocationSize;


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    public FifoFloat() {
        this.fifo = new ArrayList();
        this.appendable = null;
        this.allocationSize = DEFAULT_ALLOCATION_SIZE;
    }


    // ------------------------------------------------------------------------
    // --- methods                                                          ---
    // ------------------------------------------------------------------------

    /**
     * Only the remaining buffer content will be used by the fifo-queue.
     */
    public void put(FloatBuffer buf) {
        synchronized (fifo) {
        		this.fifo.add(buf);
        		this.avail += buf.remaining();
        		this.appendable = null;
        }
    }

    public void put(float[] f) {
        getAppendable().put(f);
        this.avail += f.length;
    }

    public void put(float[] f, int offset, int length) {
        getAppendable().put(f, offset, length);
        this.avail += length;
    }

    public void put(float f) {
        getAppendable().put(f);
    }

    public void get(float[] arr, int n) {
        if (n <= this.avail) {
        	synchronized (fifo) {
        		this.avail -= n;
        		int pos = 0;
        		while (n > 0) {
        			FloatBuffer b = (FloatBuffer)fifo.get(0);
        			int c = b.remaining();
        			if (c > n) {
        				b.get(arr, pos, n);
        				n = 0;
        			} else {
        				b.get(arr, pos, c); // whole rest of buffer
        				n -= c;
        				pos += c;
        				fifo.remove(0);
        			}
        		}
        	}
        } else {
        	throw new BufferUnderflowException();
        }
    }

    public void get(float[] arr) {
        this.get(arr, arr.length);
    }

    public float get() {
        float[] arr = new float[1];
        get(arr, 1);
        return arr[0];
    }

    public int available() {
        return this.avail;
    }

    public boolean isEmpty() {
        return (this.avail == 0);
    }

    public void ensureCapacity(int size) {
        // ??
        	if ((this.appendable == null) || (this.appendable.capacity() < size)) {
        		synchronized (fifo) {
        			this.appendable = null; // force new allocation with next put()
        			if (size > this.allocationSize) {
        				this.allocationSize = size; // next call to getAppendable will allocate appropriate amount
        			}
        		}

        	}
    }

    protected FloatBuffer getAppendable() {
        if (this.appendable == null) {
        	synchronized (fifo) {
        		this.appendable = FloatBuffer.allocate(DEFAULT_ALLOCATION_SIZE);
        		this.fifo.add(this.appendable);
        	}
        }
        return this.appendable;
    }

} // end FifoFloat
