package com.petersalomonsen.jjack.javasound;

/**
 * A generic FIFO buffer that blocks on read or write if trying to read or write
 * more than is available for reading/writing.
 * 
 * @author Peter Johan Salomonse
 *
 */
public class BlockingByteFIFO {
	byte[] buffer;
	
	long bufferPosWrite = 0;
	long bufferPosRead = 0;

	public BlockingByteFIFO(int size)
	{
		buffer = new byte[size];
	}
	
	private synchronized void unBlock()
	{
		notify();	
	}
	
	private synchronized void block()
	{
		try {
			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int read(byte[] b, int off, int len) {
		while(bufferPosWrite-bufferPosRead < len)
			block();
		
		int localBufferPos = (int)(bufferPosRead%buffer.length);
		if(localBufferPos+len > buffer.length)
		{	
			System.arraycopy(buffer, localBufferPos, b, off, buffer.length-localBufferPos);	
			System.arraycopy(buffer, 0, b, off + (buffer.length-localBufferPos), len - (buffer.length-localBufferPos));
		}
		else
			System.arraycopy(buffer, localBufferPos, b, off ,len);
	
		bufferPosRead += len;
		
		unBlock();
		return len;
	}

	public int write(byte[] b, int off, int len) {
		while(bufferPosWrite+len-bufferPosRead > buffer.length)
			block();
	
		int localBufferPos = (int)(bufferPosWrite%buffer.length);
		if(localBufferPos+len > buffer.length)
		{	
			System.arraycopy(b, off, buffer, localBufferPos, buffer.length-localBufferPos);	
			System.arraycopy(b, off + (buffer.length-localBufferPos), buffer, 0, len - (buffer.length-localBufferPos));
		}
		else
			System.arraycopy(b, off, buffer, localBufferPos,len);

		bufferPosWrite+=len;	
		
		unBlock();
		return len;
	}

}
