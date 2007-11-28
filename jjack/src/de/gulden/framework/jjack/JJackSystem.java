/*
 * Project: JJack - Java bridge API for the JACK Audio Connection Kit
 * Class:   de.gulden.framework.jjack.JJackSystem
 *
 * Licensed under the GNU Lesser General Public License (LGPL).
 * This comes with NO WARRANTY. See file License.txt for details.
 *
 * Author:  Jens Gulden
 */

package de.gulden.framework.jjack;

import java.io.File;
import java.lang.reflect.*;
import java.nio.*;
import java.util.*;

/**
 * JJack system class.
 * This purely static class is the bridge between the native JACK client
 * and the Java classes implementing <code>JJackAudioProcessor</code>.
 *  
 * @author  Jens Gulden
 */
public class JJackSystem implements JJackConstants {

    // ------------------------------------------------------------------------
    // --- final static fields                                              ---
    // ------------------------------------------------------------------------

    /**
     * JJack version number.
     */
    public static final String VERSION = "0.3";

    /**
     * Flag for debug mode.
     */
    public static final boolean DEBUG = false;

    /**
     * Name of the system property specifying the name of the native JACK client to register.
     */
    private static final String PROPERTY_CLIENT_NAME = "jjack.client.name";

    /**
     * Default name of the native JACK client to register.
     */
    private static final String DEFAULT_CLIENT_NAME = "JJack";

    /**
     * Name of the system property specifying whether JJack should give more verbose output.
     */
    private static final String PROPERTY_VERBOSE = "jjack.verbose";

    /**
     * Default verbose mode.
     */
    private static final String DEFAULT_VERBOSE = DEBUG ? "true" : "false";

    /**
     * Name of the system property specifying how many ports to allocate.
     */
    private static final String PROPERTY_PORTS = "jjack.ports";

    /**
     * Additional suffix of the system property specifying how many input ports to allocate.
     */
    private static final String SUFFIX_INPUT = ".in";

    /**
     * Suffix of the system property specifying how many output ports to allocate.
     */
    private static final String SUFFIX_OUTPUT = ".out";

    /**
     * Default ports count.
     */
    private static final String DEFAULT_PORTS = "2";

    /**
     * Suffix of the system property specifying whether ports should be auto-connected to physical JACK ports.
     */
    private static final String SUFFIX_AUTOCONNECT = ".autoconnect";

    /**
     * Default auto-connect mode.
     */
    private static final String DEFAULT_AUTOCONNECT = "false";


    // ------------------------------------------------------------------------
    // --- static fields                                                    ---
    // ------------------------------------------------------------------------

    private static String clientName;

    private static int portsInput;

    private static int portsOutput;

    private static boolean portsInputAutoconnect;

    private static boolean portsOutputAutoconnect;

    private static JJackAudioProcessor client = null;

    private static JJackException initError = null;

    private static boolean initialized;

    private static boolean running;

    private static boolean verbose;

    private static Object lock = new Object();

    /**
     * Pointer to native handle-structure.
     * Yes, this carries a real physical memory address.
     * See the native implementation of <code>libjjack.so</code>
     * in file <code>libjjack.c<code>.
     */
    private static long infPointer;


    // ------------------------------------------------------------------------
    // --- static initializer                                               ---
    // ------------------------------------------------------------------------

    static {
        String libJJackFileName=null;
        try
        {
            // try loading native library from system lib (library path)
            System.loadLibrary("jjack");
            System.out.println("native jjack library loaded using system library path");
            init1();
        } catch(Throwable e) {
            try {
                File file = new File("lib/"+System.getProperty("os.arch")+"/"+System.getProperty("os.name")+"/libjjack.so");
                libJJackFileName = file.getAbsolutePath();
                System.load(libJJackFileName);
                System.out.println("loaded jjack native library "+ libJJackFileName );
                init1();
            } catch (Throwable e2) {
                System.out.println("Could not load jjack native library");
                System.out.println("Tried system library path and " + libJJackFileName);
                e.printStackTrace();
                e2.printStackTrace();
            }
        }
    }

    static void init1() {
        // set parameters from system properties
        clientName = System.getProperty(PROPERTY_CLIENT_NAME, DEFAULT_CLIENT_NAME);
        String s = System.getProperty(PROPERTY_VERBOSE, DEFAULT_VERBOSE);
        verbose = Boolean.valueOf(s).booleanValue();
        s = System.getProperty(PROPERTY_PORTS+SUFFIX_INPUT, System.getProperty(PROPERTY_PORTS, DEFAULT_PORTS));
        portsInput = Integer.valueOf(s).intValue();
        s = System.getProperty(PROPERTY_PORTS+SUFFIX_OUTPUT, System.getProperty(PROPERTY_PORTS, DEFAULT_PORTS));
        portsOutput = Integer.valueOf(s).intValue();
        s = System.getProperty(PROPERTY_PORTS+SUFFIX_INPUT+SUFFIX_AUTOCONNECT, System.getProperty(PROPERTY_PORTS+SUFFIX_AUTOCONNECT, DEFAULT_AUTOCONNECT));
        portsInputAutoconnect = Boolean.valueOf(s).booleanValue();
        s = System.getProperty(PROPERTY_PORTS+SUFFIX_OUTPUT+SUFFIX_AUTOCONNECT, System.getProperty(PROPERTY_PORTS+SUFFIX_AUTOCONNECT, DEFAULT_AUTOCONNECT));
        portsOutputAutoconnect = Boolean.valueOf(s).booleanValue();

        // start Jack client with native library
        initialized = false;
        running = false;
        try {
            init();
            start();
        } catch (JJackException jje) {
            initError = jje;
        }
    }


    // ------------------------------------------------------------------------
    // --- constructor                                                      ---
    // ------------------------------------------------------------------------

    /**
     * Private constructor for getting an instance of Runnable.
     */
    private JJackSystem() {
        //nop
    }


    // ------------------------------------------------------------------------
    // --- static methods                                                   ---
    // ------------------------------------------------------------------------

    /**
     * Returns the sample rate used by the JACK daemon.
     *  
     * @return  the sample rate
     */
    public static native int getSampleRate();

    /**
     * Sets the audio processor which is responsible for signal processing.
     * The <code>process()</code>-method of this audio processor will then regularly be called.
     */
    public static void setProcessor(JJackAudioProcessor cl) {
        client = cl;
    }

    /**
     * Returns the audio processor which is responsible for signal processing.
     *  
     * @return  the audio processor
     */
    public static JJackAudioProcessor getProcessor() {
        return client; // may be null
    }

    /**
     * Wrapper main()-method, invokes the main()-method of the class specified as arg[0].
     * By this time, the Jack connection has already been established by the static initializer.
     *  
     * @param args command line parameters
     * @throws Throwable if an unhandled exception occurs inside the wrapped application,
     * or a JJackException if initializing the Jack client was not successful.
     */
    public static void main(String[] args) throws Throwable {
        if (initError != null) {
        	System.out.println("Error initializing JJack client:");
        	throw initError;
        }
        if (args.length==0) {
        	usage();
        }
        String cn = args[0];
        if (cn.equalsIgnoreCase("-help") || cn.equalsIgnoreCase("--help") || cn.equals("/?") || cn.equals("-?")) {
        	usage();
        }
        Class cl = null;
        try {
        	cl = Class.forName(cn);
        } catch (ClassNotFoundException cnfe) {
        	System.out.println("Error loading class '"+cn+"': "+cnfe.getMessage());
        	usage();
        }
        Method method = null;
        try {
        	method = cl.getMethod("main", new Class[] {String[].class});
        } catch (Exception e) {
        	System.out.println("Error: '"+cn+"' has no main()-method: "+e.getMessage());
        	System.exit(1);
        }
        String[] wrappedArgs = new String[args.length-1];
        for (int i=0; i < wrappedArgs.length; i++) {
        	wrappedArgs[i] = args[i+1];
        }
        try {
        	method.invoke(null, new Object[] { wrappedArgs });
        } catch (InvocationTargetException ite) {
        	throw ite.getTargetException();
        } catch (Exception ex) {
        	System.out.println("Error running main() method of '"+cn+"': "+ex.getMessage());
        	System.exit(1);
        }
    }

    public static void shutdown() throws JJackException {
        stop();

    }

    /**
     * Returns the name of the native JACK client that has been regstered by the JJack system.
     *  
     * @return  the client name
     */
    public static String getJackClientName() {
        return clientName;
    }

    /**
     * Returns the number of ports available either for input or output mode.
     *  
     * @param inout either constant <code>INPUT</code> or <code>OUTPUT</code>
     * @return  the number of ports
     */
    public static int countPorts(int inout) {
        switch (inout) {
        	case INPUT:
        		return portsInput;
        	case OUTPUT:
        		return portsOutput;
        	default:
        		return 0;
        }
    }

    /**
     * Returns whether the JJack sytem runs in verbose mode.
     *  
     * @return  true if verbose mode
     */
    public static boolean verbose() {
        return verbose;
    }

    /**
     * Logs a message from the specified source object, if verbose mode is enabled.
     *  
     * @param src the source object, usually the originator of the log message
     * @param msg the message text
     */
    public static void log(Object src, String msg) {
        if (verbose()) {
        	String s;
        	if (src instanceof JJackMonitor) {
        		s = ((JJackMonitor)src).getName()+" ["+src.getClass().getName()+"]";
        	} else {
        		s = src.getClass().getName();
        	}
        	System.out.println(s+": "+msg);
        }
    }

    /**
     * Calculates number of samples used for a single channel
     * in the given amount of milliseconds.
     * The current system sample rate is being taken into account.
     *  
     * @return  number of sample values
     */
    public static int calculateSampleCount(int milliseconds) {
        int sr = getSampleRate();
        int count = (sr * milliseconds) / 1000;
        return count;
    }

    /**
     * Process an audio event by a given audio processor.
     *  
     * @param p the audio processor that is to process the event
     * @param e the audio event
     */
    public static void process(JJackAudioProcessor p, JJackAudioEvent e) {
        boolean monitorable = (p instanceof JJackAudioProcessorMonitorable);
        if (monitorable) {
        	// inform listeners that processing starts. p might be among them.
        	for (Iterator it = ((JJackAudioProcessorMonitorable)p).getAudioProcessListeners().iterator(); it.hasNext(); ) {
        		JJackAudioProcessListener l = (JJackAudioProcessListener)it.next();
        		l.beforeProcess(e);
        	}
        }

        p.process(e);

        if (monitorable) {
        	// inform listeners that processing has ended. p might be among them.
        	for (Iterator it = ((JJackAudioProcessorMonitorable)p).getAudioProcessListeners().iterator(); it.hasNext(); ) {
        		JJackAudioProcessListener l = (JJackAudioProcessListener)it.next();
        		l.afterProcess(e);
        	}
        }
    }

    /**
     * Returns a short information text about the JJack system and its current status.
     *  
     * @return  information text
     */
    public static String getInfo() {
        return
        "JJack version "+VERSION+"\n" + 		"\n" + 		"Client name: "+getJackClientName()+"\n" + 		"Sample rate: "+getSampleRate()+"\n" +
        "# Input ports: "+countPorts(INPUT)+"\n" +
        "# Output ports: "+countPorts(OUTPUT)+"\n" +
        "";
    }

    /**
     * Output usage description, then exit with error code.
     */
    private static void usage() {
        System.out.println("usage: java ... (-D" + PROPERTY_CLIENT_NAME + "=Name) JJack <wrapped-class-with-main-method> <arg0 of wrapped-class> <arg1 of wrapped-class> ...");
        System.exit(1);
    }

    /**
     * Native initialization.
     */
    private static native void nativeInit() throws JJackException;

    /**
     * Native client start.
     */
    private static native void nativeStart() throws JJackException;

    /**
     * Native client shutdown.
     */
    private static native void nativeDestroy() throws JJackException;

    /**
     * Initialized the JJack system.
     */
    private static void init() throws JJackException {
        nativeInit();
        initialized = true;
    }

    /**
     * Start background processing on the Java side.
     *  
     * @throws JJackException if an error occurs
     */
    private static void start() throws JJackException {
        if (!initialized) {
        	init();
        }
        boolean wasRunning;
        synchronized (lock) {
        	wasRunning = running;
        	running = true;
        }
        if (!wasRunning) {
        	nativeStart();
        }
    }

    /**
     * Stops background processing on the Java side.
     * (Not implemented.)
     */
    private static void stop() {
        // nop (exit is performed by shutting down the JVM which lets the JACK client become 'zombified')
    }

    /**
     * This method is the actual bridge from JACK to Java.
     * It is directly called from native code, and only from there.
     *  
     * @param in the direct memory access input buffer
     * @param out the direct memory access output buffer
     */
    private static void processBytes(ByteBuffer[] in, ByteBuffer[] out) {
        JJackAudioProcessor cl = client; // copy reference to avoid synchronization problems (just in case)
        if (cl != null) {
        	FloatBuffer[] inAsFloat = new FloatBuffer[in.length];
        	for (int i=0; i<in.length; i++) {
        		inAsFloat[i] = in[i].order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        	}
        	FloatBuffer[] outAsFloat = new FloatBuffer[out.length];
        	for (int i=0; i<out.length; i++) {
        		outAsFloat[i] = out[i].order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
        	}
        	JJackAudioEvent e = new JJackAudioEvent(System.currentTimeMillis(), cl, inAsFloat, outAsFloat);
        	process(cl, e);
        }
    }

    public static boolean isInitialized()
    {
    	return initialized;
    }
} // end JJackSystem
