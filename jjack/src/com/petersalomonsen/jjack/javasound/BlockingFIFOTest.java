package com.petersalomonsen.jjack.javasound;

/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   BlockingFIFOTest
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
 * Simple test of the FIFO buffer - verifying that the data that goes in first comes out first
 * @author Peter Johan Salomonsen
 */
class BlockingFIFOTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final byte[] write = new byte[] {0,1,2,3,4,5,6,7,8,9};
		final byte[] read = new byte[2];
		
		final BlockingByteFIFO bfifo = new BlockingByteFIFO(100);
		
		new Thread() {
			public void run()
			{
				while(true)
					bfifo.write(write, 0, write.length);
			}
		}.start();

		new Thread() {
			public void run()
			{
				int n = 0;
				
				while(true)
				{
					bfifo.read(read, 0, read.length);
					
					//for(byte b : read)
					for (int i = 0; i < read.length; i++)
					{
						byte b = read[i];
						System.out.println(b+" "+(n%10));
						if(n%10!=b)
							System.out.println("ERROR");
						n++;
					}
					
					try
					{
						Thread.sleep(1);
					} catch(Exception e) {}
				}
			}
		}.start();

	}

}
