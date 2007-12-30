/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   JJackQuickClientLaunch
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file LICENSE for details.
 *
 * Author:  Peter Johan Salomonsen
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import de.gulden.framework.jjack.JJackAudioProcessor;

/**
 * Quick launcher class for launching built in jjack clients out of the box...
 * 
 * @author Peter Johan Salomonsen
 */
public class JJackQuickClientLaunch {

	/**
	 * Translates a class uri to a java-style path
	 * @param uri
	 * @return a classname that can be used by Class.forName()
	 */
	public static final String translateToJavaPath(String uri)
	{
		// Translate into classname, and also handle subclasses
		String className = uri.replace(".class","");
		if(className.indexOf("$1")>0)
			className = className.substring(0,className.indexOf("$1"));
		className = className.replace("/",".");
		return className;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/**
		 * Find jjack clients in built in client package
		 */
		String basePath = de.gulden.application.jjack.clients.Gain.class.getPackage().getName().replace('.', '/');
		System.out.println("Scanning classes in: "+basePath);
		
		File dir = new File(ClassLoader.getSystemResource(basePath).getPath());
				
		//List<Class> clients = new ArrayList<Class>();
		List clients = new ArrayList();
		
		//for(File file : dir.listFiles())
		File[] files =  dir.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			File file = files[i];
			if(!file.isDirectory())
			{
				String relativePath = file.getPath();
				relativePath = relativePath.substring(relativePath.indexOf(basePath));
				String className = translateToJavaPath(relativePath);
				try
				{
					Class cls = Class.forName(className);
					if(JJackAudioProcessor.class.isAssignableFrom(cls))
					{
						System.out.println("Found client: "+cls.getName());
						clients.add(cls);
					}
						
				} catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}
		}
		
		// Show a dialog displaying the clients
		try
		{
			JJack.main(new String[] {((Class)JOptionPane.showInputDialog(null, "Select client", "Select client", JOptionPane.QUESTION_MESSAGE, null, clients.toArray(), null)).getName()});
		} catch(NullPointerException e)
		{
			System.out.println("No client was selected.. Exiting.. ");
		}
	}

}
