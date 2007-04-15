package com.petersalomonsen.jjack.javasound;

public class BlockingFIFOTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final byte[] write = new byte[] {0,1,2,3,4,5,6,7,8,9};
		final byte[] read = new byte[15];
		
		final BlockingByteFIFO bfifo = new BlockingByteFIFO(35);
		
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
					
					for(byte b : read)
					{
						System.out.println(b+" "+(n%10));
						if(n%10!=b)
							System.out.println("ERROR");
						n++;
					}
					
					try
					{
						Thread.sleep(1000);
					} catch(Exception e) {}
				}
			}
		}.start();

	}

}
