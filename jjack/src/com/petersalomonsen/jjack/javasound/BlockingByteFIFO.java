package com.petersalomonsen.jjack.javasound;

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   BlockingByteFIFO
 * Version: 0.3
 *
 * Date:    2007-04-09
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Peter Johan Salomonsen
 */

/**
 * A FIFO byte buffer that blocks on read or write if trying to read or write
 * more than is available for reading/writing.
 * 
 * @author Peter Johan Salomonsen
 *
 */
public class BlockingByteFIFO {
	byte[] buffer;
	
	long bufferPosWrite = 0;
	long bufferPosRead = 0;

	// Only one thread should be blocked at the time - we monitor this here...
	
	boolean blocking = false;
	
	public BlockingByteFIFO(int size)
	{
		buffer = new byte[size];
	}
	
	private synchronized void unblock()
	{
		notify();
		blocking = false;
	}
	
	private synchronized void block()
	{
		if(!blocking)
		{
			blocking = true;
			
			try {	
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int availableRead()
	{
		return (int)(bufferPosWrite - bufferPosRead);
	}
	
	public int availableWrite()
	{
		return buffer.length - availableRead();
	}
	
	/**
	 * Read all requested amount of data, or the maximum that is available for reading
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	private int readLenOrAvailable(byte[] b,int off,int len)
	{
		int availableRead = availableRead();
		if(availableRead < len)
			len = availableRead;
		
		if(len>0)
		{
			int localBufferPos = (int)(bufferPosRead%buffer.length);
			if(localBufferPos+len > buffer.length)
			{	
				System.arraycopy(buffer, localBufferPos, b, off, buffer.length-localBufferPos);	
				System.arraycopy(buffer, 0, b, off + (buffer.length-localBufferPos), len - (buffer.length-localBufferPos));
			}
			else
				System.arraycopy(buffer, localBufferPos, b, off ,len);
		
			bufferPosRead += len;
	
			// Unlock write blocks waiting to write
			unblock();
		}
		return len;
	}

	/**
	 * Write all requested amount of data, or the maximum that is available for writing
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	private int writeLenOrAvailable(byte[] b, int off, int len)
	{
		int availableWrite = availableWrite();
		if(availableWrite < len)
			len = availableWrite;

		if(len>0)
		{
			int localBufferPos = (int)(bufferPosWrite%buffer.length);
			if(localBufferPos+len > buffer.length)
			{	
				System.arraycopy(b, off, buffer, localBufferPos, buffer.length-localBufferPos);	
				System.arraycopy(b, off + (buffer.length-localBufferPos), buffer, 0, len - (buffer.length-localBufferPos));
			}
			else
				System.arraycopy(b, off, buffer, localBufferPos,len);
	
			bufferPosWrite+=len;
			// Unblock read blocks waiting to read
			unblock();
		}
		
		return len;
	}

	/**
	 * Read into provided byteArray - will block until all data is read
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	public int read(byte[] b, int off, int len) {
		int readCount = 0;
		
		while(readCount < len)
		{
			// Read as much as possible
			readCount += readLenOrAvailable(b,off+readCount,len-readCount);
		
			// If we couldn't read it all, then block until more is available
			if(readCount<len)
				block();
		}
		
		return len;
	}

	
	/**
	 * Write data of provided bytearray. Will block until all data is written.
	 * @param b
	 * @param off
	 * @param len
	 * @return
	 */
	public int write(byte[] b, int off, int len) {
		int writeCount = 0;
		
		while(writeCount < len)
		{
			// Write as much as possible
			writeCount += writeLenOrAvailable(b,off+writeCount,len-writeCount);
			
			// If we couldn't write it all, then block until more is available
			if(writeCount<len)
				block();
		}
		
		return len;
	}

}
