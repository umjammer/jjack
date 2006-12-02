/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.application.jjack.JJack
 * Version: 0.2
 *
 * Date:    2004-11-16
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file license.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.application.jjack;

import de.gulden.framework.jjack.*;
import de.gulden.util.Toolbox;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.FontUIResource;

/**
 * JJack shell application to run audio processor clients.
 *  
 * @author  Jens Gulden
 * @version  0.2
 */
public class JJack {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * Usage message.
     */
    protected static final String USAGE = "usage: ... java ... JJack {-i <package-import>} <client> { '[' { <monitor-client> } ']' } { <client> { '[' { <monitor-client> } ']' } } | -c <config-file> | -help";

    /**
     * Icon resource for main window.
     */
    protected static final String ICON = "de/gulden/application/jjack/res/icon.png";


    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------

    /**
     * Font, normal size.
     */
    protected static Font FONT = new Font("Dialog", Font.PLAIN, 9);

    /**
     * Font, bigger size.
     */
    protected static Font FONT_BIG = new Font("Dialog", Font.PLAIN, 10);

    /**
     * Imported packages to look for client classes.
     */
    private static Collection imports;

    /**
     * Flag to remember if any of the clients is visible.
     */
    private static boolean visibleMode;

    /**
     * Tokenizer for the command line string.
     */
    private static StringTokenizer st;

    /**
     * Current token during parsing phase.
     */
    private static String tok;


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    /**
     * Application entry method.
     *  
     * @param args command line parameters
     * @see  JJack#main
     */
    public static void main(String[] args) {
        initSwing();
        String a = Toolbox.implode(args, " ");
        initTokenizer(a);
        visibleMode = false;
        // parse options
        imports = new TreeSet();
        nextToken();
         while ((tok!=null) && tok.startsWith("-")) {
        	parseOption();
         }
         // parse client chain
         requireToken();
         JJackAudioProcessor firstProcessor = parseChain();
        // open window
        if (visibleMode) {
        	JFrame frame = new JFrame(JJackSystem.getJackClientName());
        	frame.addWindowListener(
        		new WindowAdapter() {
        			public void windowClosing(WindowEvent e) {
        				try {
        					JJackSystem.shutdown();
        				} catch (JJackException jje) {
        					//nop
        				}
        				System.exit(0);
        			}
        		}
        	);
        	ImageIcon icon = new ImageIcon(ClassLoader.getSystemResource(ICON));
        	frame.setIconImage(icon.getImage());
        	JComponent l = layout(firstProcessor);
        	frame.getContentPane().add(l);
        	Point framepos = new Point(100, 100);
        	// add random coordinates to not directly overlap multiple windows from different JVMs
        	int rnd =  (int) (20 * Math.random());
        	framepos.x += rnd;
        	framepos.y += rnd;
        	frame.setLocation(framepos);
        	frame.pack();
        	frame.setVisible(true);
        }
        // connect to Jack
        JJackSystem.setProcessor(firstProcessor);
    }

    /**
     * Output usage message, then exit.
     */
    private static void usage() {
        System.out.println(USAGE);
        System.exit(1);
    }

    /**
     * Output usage message and help, then exit.
     */
    private static void help() {
        System.out.println(USAGE);
        System.out.println("clients are Java classes that implement interface de.gulden.framework.jjack.JJackAudioProducer");
        System.out.println("monitor-clients are classes that implement interface de.gulden.framework.jjack.JJackAudioConsumer");
        System.exit(1);
    }

    /**
     * Lays out all visible components a JJackAudioProcessor and all following JJackAudioProcessors connected to it.
     * Chained processors are layouted horizontally, heading to the right direction.
     * Monitor processors are layouted vertically, heading to the bottom.
     *  
     * @param p first audio processor to layout
     * @return  container component with all layouted visible components
     */
    private static JComponent layout(JJackAudioProcessor p) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridwidth = 1;
        gc.gridheight = 1;
        gc.gridx = 0;
        gc.gridy = GridBagConstraints.RELATIVE;
        while (p != null) {
        	JComponent pComponent;
        	if (p instanceof JComponent) { // visible component
        		pComponent  = (JComponent)p;
        	} else { // invisible component
        		String name;
        		if (p instanceof JJackMonitor) {
        			name = ((JJackMonitor)p).getName();
        		} else {
        			name = "(invisible)";
        		}
        		pComponent = new JLabel(name);
        	}
        	if (p instanceof JJackAudioProducer) {
        		// monitors
        		Collection monitors = ((JJackAudioProducer)p).getMonitors();
        		if ( ! monitors.isEmpty() ) {
        			addAsToolbar(panel, pComponent, gc);
        			for (Iterator it = monitors.iterator(); it.hasNext(); ) {
        				JJackAudioProcessor m=(JJackAudioProcessor)it.next();
        				JComponent c = layout(m); // recursion
        				int gcAnchorBack = gc.anchor;
        				if ( ! it.hasNext() ) {
        					gc.gridheight = GridBagConstraints.REMAINDER;
        					gc.anchor = GridBagConstraints.NORTH;
        				}
        				panel.add(c, gc);
        				gc.anchor = gcAnchorBack;
        			}
        		} else { // no monitors following
        			gc.gridheight = GridBagConstraints.REMAINDER;
        			addAsToolbar(panel, pComponent, gc);
        		}
        		// chained
        		p = ((JJackAudioProducer)p).getChained();
        	} else {
        		addAsToolbar(panel, pComponent, gc);
        		p = null;
        	}
        	gc.gridx = GridBagConstraints.RELATIVE; // for all following columns (0 for first one)
        	gc.gridheight = 1;
        }
        return panel;
    }

    /**
     * Adds a component to a container, after surrounding it with a JToolbar.
     *  
     * @param parent the container-component where the component is to be added to
     * @param p the component
     */
    private static void addAsToolbar(Container parent, JComponent p, Object constraints) {
        	JToolBar tb;
        	String name;
        	MouseListener ml;
        	if (p instanceof JJackMonitor) {
        		name = ((JJackMonitor)p).getName();
        		tb = new JToolBar(name);
        		ml = new JJackInfoBoxMouseListener((JJackMonitor)p);
        	} else {
        		name = null;
        		tb = new JToolBar();
        		ml = null;
        	}
        	tb.setBorderPainted(true);
        	TitledBorder border = new TitledBorder(name);
        	border.setTitleFont(FONT);
        	p.setBorder(border);
        	tb.add(p);
        	JPanel panel = new JPanel(); // panel to allow removing and re-adding ToolBars as individual windows
        	panel.add(tb);
        	if (ml != null) {
        		panel.addMouseListener(ml);
        		tb.addMouseListener(ml);
        	}
        	parent.add(panel, constraints);
    }

    /**
     * Parses a client chain parameter string, instanciates and interconnected the client objects.
     *  
     * @return  the first client in chain, with other clients interconnected to it
     */
    private static JJackAudioProcessor parseChain() {
        if ((tok==null) || tok.equals("]")) {
        	if (tok!=null) {
        		nextToken(); // after ']'
        	}
        	return null;
        } else {
        	JJackAudioProcessor p = create(tok);
        	nextToken();
        	parseParameters(p);
        	while ((tok!=null) && tok.equals("[")) { // monitors following
        		nextTokenRequired();
        		JJackAudioProcessor m = parseChain(); // recursion
        		connectMonitor(p, m);
        	}
        	if (tok!=null) { // more chained following
        		JJackAudioProcessor p2 = parseChain(); // recursion
        		if (p2!=null) {
        			connectChain(p, p2);
        		}
        	}
        	return p;
        }
    }

    /**
     * Connects a monitor-client to another client.
     *  
     * @param a the client to connect the monitor to
     * @param b the monitor to be connected
     */
    private static void connectMonitor(JJackAudioProcessor a, JJackAudioProcessor b) {
        JJackAudioProducer pa = (JJackAudioProducer)cast(a, JJackAudioProducer.class);
        JJackAudioConsumer pb = (JJackAudioConsumer)cast(b, JJackAudioConsumer.class);
          pa.addMonitor(pb);
    }

    /**
     * Chains a client to another client.
     *  
     * @param a the client to chain the other client to
     * @param b the client to be chained
     */
    private static void connectChain(JJackAudioProcessor a, JJackAudioProcessor b) {
        JJackAudioProducer pa = (JJackAudioProducer)cast(a, JJackAudioProducer.class);
        JJackAudioProducer pb = (JJackAudioProducer)cast(b, JJackAudioProducer.class);
          pa.setChained(pb);
    }

    /**
     * Parses parameters for the given audio processor.
     * A parameter list is given in round brackets. It may be missing to use deault parameters only.
     *  
     * @param p client whose parameters are to be set
     */
    private static void parseParameters(JJackAudioProcessor p) {
        if ((tok!=null) && tok.equals("(")) {
        	nextTokenRequired();
        	boolean close = false;
        	do {
        		close = tok.equals(")");
        		if (!close) {
        			String pName = tok;
        			String pValue = null;
        			nextTokenRequired();
        			if (tok.equals("=")) {
        				nextTokenRequired();
        				pValue = tok;
        				nextTokenRequired();
        			}
        			assignParameter(p, pName, pValue);
        			if (tok.equals(",")) {
        				nextTokenRequired();
        			}
        		}
        	} while (!close);
        	nextToken();
        }
    }

    /**
     * Instantiates an instance of the audio processor class given as string.
     *  
     * @param classname the class name of a class implementinf JJackAudioProcessor
     * @return  the audio processor instance.
     */
    private static JJackAudioProcessor create(String classname) {
        Class cl = findClass(classname);
        try {
        	Object o = cl.newInstance();
        	if (o instanceof Component) {
        		visibleMode = true; // at least one processor visible
        	}
        	return (JJackAudioProcessor)cast(o, JJackAudioProcessor.class);
        } catch (Exception e) {
        	error("cannot create instance of class '"+cl.getName()+"'");
        	return null;
        }
    }

    /**
     * Find the class named by classname, using imported packages as search paths.
     *  
     * @param classname Unqualified or fully qualified classname. If unqualified, an appropriate import package must have been specified before.
     * @return  The class. null will never be returned, a global error is thrown if not found.
     */
    private static Class findClass(String classname) {
        // try to get directly
        try {
        	return Class.forName(classname);
        } catch (ClassNotFoundException cnfe) {
        	// try to qualify by imports
        	for (Iterator it = imports.iterator(); it.hasNext(); ) {
        		String packagePrefix = (String)it.next();
        		try {
        			return Class.forName(packagePrefix+"."+classname);
        		} catch (ClassNotFoundException cnfe2) {
        			// nop
        		}
        	}
        	// if reached here: not found
        	error("cannot find class '"+classname+"'");
        	return null;
        }
    }

    /**
     * Set parameter value through runtime reflection.
     *  
     * @param o the object on which a field is to be set
     * @param valueStr the string representation of the value to be set, will be converted to the field's type
     */
    private static void assignParameter(Object o, String paramname, String valueStr) {
        Class cl = o.getClass();
        String settername = "set"+Toolbox.capitalize(paramname);
        // find setter method
        Method[] mm = cl.getMethods();
        for (int i=0; i<mm.length; i++) {
        	Method m = mm[i];
        	if (m.getName().equals(settername)) { // matching name
        		Class[] sig = mm[i].getParameterTypes();
        		if (sig.length==1) { // matching single-param signature
        			Class type = sig[0];
        			try {
        				if (Boolean.class.isAssignableFrom(type)) {
        					if (valueStr==null) {
        						valueStr = "true";
        					}
        					boolean b = Toolbox.isTrue(valueStr);
        					m.invoke(o, new Object[] {b?Boolean.TRUE:Boolean.FALSE});
        					return;
        				} else {
        					if (valueStr==null) {
        						error("cannot assign an empty value to parameter '"+paramname+"'");
        					}
        					if (String.class.isAssignableFrom(type)) {
        						m.invoke(o, new Object[] {valueStr});
        						return;
        					} else if (int.class.isAssignableFrom(type)) {
        						int n = Integer.parseInt(valueStr);
        						m.invoke(o, new Object[] {new Integer(n)});
        						return;
        					} else if (float.class.isAssignableFrom(type)) {
        						float n = Float.parseFloat(valueStr);
        						m.invoke(o, new Object[] {new Float(n)});
        						return;
        					} else if (double.class.isAssignableFrom(type)) {
        						double n = Double.parseDouble(valueStr);
        						m.invoke(o, new Object[] {new Double(n)});
        						return;
        					}
        				}
        			} catch (NumberFormatException nfe) {
        				error("cannot set '"+paramname+"="+(valueStr!=null?valueStr:"true")+"' - not a valid number");
        			} catch (IllegalAccessException iae) {
        				error("cannot set '"+paramname+"="+(valueStr!=null?valueStr:"true")+"' - illegal access");
        			} catch (InvocationTargetException ite) {
        				error("cannot set '"+paramname+"="+(valueStr!=null?valueStr:"true")+"' - invocation target exception: "+ite.getCause().getClass().getName()+": "+ite.getCause().getMessage());
        			}
        		}
        	}
        }
        // if reached here: not found
        error("cannot set '"+paramname+"="+(valueStr!=null?valueStr:"true")+"' - no setter method '"+settername+"'");
    }

    /**
     * Parses a single command line option switch.
     */
    private static void parseOption() {
        // import package
        if (tok.equals("-i")) {
        	nextTokenRequired();
        	imports.add(tok);
        	nextTokenRequired();
        // read config from file
        } else if (tok.equals("-c")) {
        	nextTokenRequired();
        	try {
        		FileReader f = new FileReader(tok);
        		StringBuffer sb = new StringBuffer();
        		char[] cc;
        		int len;
        		do {
        			cc = new char[10000];
        			len = f.read(cc);
        			if (len!=-1) { // buffer was sufficient
        				sb.append(cc, 0, len);
        			} else {
        				sb.append(cc);
        			}
        		} while (len==-1);
        		initTokenizer(sb.toString());
        		nextTokenRequired();
        	} catch (IOException ioe) {
        		error("cannot read configuration from file - "+ioe.getClass().getName()+": "+ioe.getMessage());
        	}
        // help
        } else if (tok.equals("-h")||tok.equals("-help")||tok.equals("--h")||tok.equals("--help")||tok.equals("-?")||tok.equals("/?")) {
        	   help();
        // unkown
        } else {
        	error("unknown option '"+tok+"'");
        }
    }

    /**
     * Initialize the command line tokenizer.
     *  
     * @param s complete command line string (joined array components of String[] args, or loaded from a text file)
     */
    private static void initTokenizer(String s) {
        st = new StringTokenizer(s, " \t\n\r[]()=,", true);
    }

    /**
     * Reads next token into global variable <code>tok</code>.
     * If no more tokens are available, <code>tok</code> becomes null.
     */
    private static void nextToken() {
        try {
        	do {
        		// repeat, skip all whitespaces
        	} while ((tok = st.nextToken()).trim().length()==0);
        } catch (NoSuchElementException e) {
        	tok = null;
        }
    }

    /**
     * Makes sure that the previous call to <code>nextToken()</code> leads to a next valid token.
     * Otherwise outputs usage message and exits.
     */
    private static void requireToken() {
        if (tok==null) {
        	usage();
        }
    }

    /**
     * Reads next token and makes sure that it is a valid one.
     * Otherwise outputs usage message and exits.
     */
    private static void nextTokenRequired() {
        nextToken();
        requireToken();
    }

    /**
     * Tests if a class cast would succeed.
     * If it would fail, outputs a detailed error message and exit.
     *  
     * @param o instance to be casted
     * @param castTo type to cast to
     * @return  instance o again, to allow performing the actual cast right after calling this
     */
    private static Object cast(Object o, Class castTo) {
        if (!castTo.isAssignableFrom(o.getClass())) {
        	error("cannot use "+o.getClass().getName()+" as "+castTo.getName());
        }
        return o;
    }

    /**
     * Outputs a text message.
     *  
     * @param msg text message to output
     */
    private static void message(String msg) {
        System.out.println(msg);
    }

    /**
     * Outputs an error message and exits.
     *  
     * @param msg error message to output
     */
    private static void error(String msg) {
        message(msg);
        System.exit(1);
    }

    /**
     * Initialized some Swing GUI elements with a smaller default font.
     *  
     * @see  FONT_BIG
     */
    private static void initSwing() {
        UIDefaults ui = UIManager.getDefaults();
        FontUIResource font = new FontUIResource(FONT_BIG);
        ui.put("Menu.font", font);
        ui.put("MenuItem.font", font);
        ui.put("PopupMenu.font", font);
        ui.put("Label.font", font); // e.g. for info-message-box
    }

} // end JJack
