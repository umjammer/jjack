/*
 * Project:  JJack - Java bridge API for the JACK Audio Connection Kit
 *
 * Module:   libjjack.so
 *           Native implementation of Jack-to-Java bridge.
 *
 * Version:  0.2
 * Date:     2004-11-16
 * Author:   J. Gulden
 * Modified by: Peter J. Salomonsen
 * 
 * 2006-12-17 (PJS):	Changed INF pointer to long (64bit) for amd64 support
 * 
 * Compile commands:
 * gcc -fPIC -I/usr/java/java/include -I/usr/java/java/include/linux -I/usr/include/jack -c libjjack.c
 * gcc -shared -fPIC -ljack -o libjjack.so libjjack.o
 */

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <jni.h>
#include <jack.h>
#include "libjjack.h"

/*
 * Maximum number of ports to be allocated. 
 */
#define MAX_PORTS 64

/*
 * Symbols for input, output. 
 */
typedef enum {
    INPUT, 
    OUTPUT, 
    MODES   /* total count */
} MODE;

/*
 * Constant strings. Some of these reflect the names of Java identifiers. 
 */
const char *CLASS_BYTEBUFFER = "java/nio/ByteBuffer";
const char *CLASS_JJACKSYSTEM = "de/gulden/framework/jjack/JJackSystem";
const char *CLASS_JJACKEXCEPTION = "de/gulden/framework/jjack/JJackException";
const char *METHOD_PROCESS = "processBytes";
const char *METHOD_PROCESS_SIG = "([Ljava/nio/ByteBuffer;[Ljava/nio/ByteBuffer;)V";
const char *FIELD_INFPOINTER = "infPointer";
const char *FIELD_CLIENTNAME = "clientName";
const char *FIELD_PORTCOUNT[MODES] = { "portsInput", "portsOutput"};
const char *FIELD_PORTAUTOCONNECT[MODES] = { "portsInputAutoconnect", "portsOutputAutoconnect"};
const char *MODE_LABEL[MODES] = { "input", "output"};
const unsigned long MODE_JACK[MODES] = { JackPortIsInput, JackPortIsOutput };

/*
 * Global memory to store values needed for performing the Java callback
 * from the JACK thread. A pointer to this structure is passed to the 
 * process()-function as argument 'void *arg'. Each running instance of 
 * JJackSystem allocates exactly one block of memory for this structure.
 * When Java calls native code, the pointer to this structure is taken
 * from the field 'infPointer' of class JJackSystem (a Java field holds a real
 * physical pointer there). 
*/
typedef struct Inf {
    JNIEnv *env;                    /* environment pointer inside JACK thread, assigned by AttachCurrentThread() */
    JavaVM *jvm;                    /* jvm pointer */
    jack_client_t *client;
    int portCount[MODES];
    int portAutoconnect[MODES];     /* (boolean) */
    jack_port_t *port[MODES][MAX_PORTS];
    jclass cls_JJackSystem;         /* handle of class JJackSystem */
    jclass cls_ByteBuffer;          /* handle of class java.nio.ByteBuffer */
    jmethodID mid_process;          /* handle of method JJackSystem.process()*/
} *INF;


/* ------------------------------------------------------------------------ */
/* --- Private functions                                                    */
/* ------------------------------------------------------------------------ */

/*
 * Throw a JJackException in Java with an optional second description text. 
 */
void throwExc2(JNIEnv *env, char *msg, char *msg2) {	
    jclass clsExc = (*env)->FindClass(env, CLASS_JJACKEXCEPTION);	
    char m[255] = "";
    if (msg != NULL) {
        strcat(m, msg);
    }
    if (msg2 != NULL) { /* optional second string */
        strcat(m, msg2);
    }
    if (clsExc == 0) {
        printf("fatal: cannot access class JJackException.\nerror:\n%s\n", m);
    } else {
        (*env)->ThrowNew(env, clsExc, m);
    }
}	

/*
 * Throw a JJackException in Java with a description text. 
 */
void throwExc(JNIEnv *env, char *msg) {	
    throwExc2(env, msg, NULL);
}

/*
 * Get the value of an integer-field of an object instance. 
 */
jint getIntField(JNIEnv *env, jobject obj, char *name) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "I");
    jint i;
    if (fid == 0) {
        throwExc2(env, "cannot access int field: ", name);
        return -1;
    }
    i = (*env)->GetIntField(env, obj, fid);
    return i;
}

/*
 * Set the value of an integer-field of an object instance. 
 */
void setIntField(JNIEnv *env, jobject obj, char *name, jint val) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "I");
    if (fid == 0) return throwExc2(env, "cannot access int field: ", name);
    (*env)->SetIntField(env, obj, fid, val);
}

/* 
 * Get the value of a long-field of an object instance. 
 */
jint getLongField(JNIEnv *env, jobject obj, char *name) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "J");
    jlong l;
    if (fid == 0) {
        throwExc2(env, "cannot access long field: ", name);
        return -1;
    }
    l = (*env)->GetLongField(env, obj, fid);
    return l;
}

/* 
 * Set the value of a long-field of an object instance. 
 */
void setLongField(JNIEnv *env, jobject obj, char *name, jlong val) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "J");
    if (fid == 0) return throwExc2(env, "cannot access long field: ", name);
    (*env)->SetLongField(env, obj, fid, val);
}

/*
 * Get the value of a boolean-field of an object instance.
 */
jboolean getBooleanField(JNIEnv *env, jobject obj, char *name) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "Z");
    jboolean b;
    if (fid == 0) {
        throwExc2(env, "cannot access boolean field: ", name);
        return -1;
    }
    b = (*env)->GetBooleanField(env, obj, fid);
    return b;
}

/*
 * Set the value of a boolean-field of an object instance.
 */
void setBooleanField(JNIEnv *env, jobject obj, char *name, jboolean val) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "Z");
    if (fid == 0) return throwExc2(env, "cannot access boolean field: ", name);
    (*env)->SetBooleanField(env, obj, fid, val);
}

/*
 * Get the value of a String-field of an object instance.
 */
jstring getStringField(JNIEnv *env, jobject obj, char *name) {
    jclass cls = (*env)->GetObjectClass(env, obj);
    jfieldID fid = (*env)->GetFieldID(env, cls, name, "Ljava/lang/String;");
    jstring js;
    if (fid == 0) {
        throwExc2(env, "cannot access string field: ", name);
        return NULL;
    }
    js = (*env)->GetObjectField(env, obj, fid);
    return js;
}

/*
 * Get the value of a static integer-field of a class. 
 */
jint getStaticIntField(JNIEnv *env, jclass cls, char *name) {
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, name, "I");
    jint i;
    if (fid == 0) {
        throwExc2(env, "cannot access int field: ", name);
        return -1;
    }
    i = (*env)->GetStaticIntField(env, cls, fid);
    return i;
}

/*
 * Set the value of a static integer-field of a class. 
 */
void setStaticIntField(JNIEnv *env, jclass cls, char *name, jint val) {
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, name, "I");
    if (fid == 0) return throwExc2(env, "cannot access int field: ", name);
    (*env)->SetStaticIntField(env, cls, fid, val);
}

/*
 * Get the value of a static long-field of a class. 
 */
jlong getStaticLongField(JNIEnv *env, jclass cls, char *name) {
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, name, "J");
    jlong i;
    if (fid == 0) {
        throwExc2(env, "cannot access int field: ", name);
        return -1;
    }
    i = (*env)->GetStaticLongField(env, cls, fid);
    return i;
}

/*
 * Set the value of a static long-field of a class. 
 */
void setStaticLongField(JNIEnv *env, jclass cls, char *name, jlong val) {
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, name, "J");
    if (fid == 0) return throwExc2(env, "cannot set long field: ", name);
    (*env)->SetStaticLongField(env, cls, fid, val);
}

/*
 * Get the value of a static boolean-field of a class.
 */
jint getStaticBooleanField(JNIEnv *env, jclass cls, char *name) {
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, name, "Z");
    jboolean b;
    if (fid == 0) {
        throwExc2(env, "cannot access static boolean field: ", name);
        return -1;
    }
    b = (*env)->GetStaticBooleanField(env, cls, fid);
    return b;
}

/*
 * Get the value of a static String-field of a class.
 */
jstring getStaticStringField(JNIEnv *env, jclass cls, char *name) {
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, name, "Ljava/lang/String;");
    jstring js;
    if (fid == 0) {
        throwExc2(env, "cannot access string field: ", name);
        return NULL;
    }
    js = (*env)->GetStaticObjectField(env, cls, fid);
    return js;
}

/*
 * Allocate string memory. 
 */
const char *allocchars(JNIEnv *env, jstring js) {
    return (*env)->GetStringUTFChars(env, js, 0);
}

/*
 * Deallocate string memory.
 */
void freechars(JNIEnv *env, jstring js, const char *s) {
    (*env)->ReleaseStringUTFChars(env, js, s);
}

/*
 * Get pointer to Inf-struct from 'infPointer'-field of class JJackSystem. 
 */
INF getInf(JNIEnv *env, jclass cls) {	
    INF inf = (INF) getStaticLongField(env, cls, FIELD_INFPOINTER);
    return inf;
}

/* ------------------------------------------------------------------------- */
/* --- Callback handlers                                                     */
/* ------------------------------------------------------------------------- */

/* 
 * Main JACK audio process chain callback. From here, we will branch into the
 * Java virtual machine to let Java code perform the processing.
 */
int process(jack_nframes_t nframes, void *arg) {
    INF inf = arg;
    JNIEnv *env;
    JavaVM *jvm = inf->jvm;
    jobject byteBuffer;
    jobjectArray byteBufferArray[MODES];
    int err;
    int mode, i;

    /* attach to JVM, if not yet done */
    if (inf->env == NULL) {
        err = (*jvm)->AttachCurrentThread(jvm, &inf->env, NULL); /* writes env-pointer into inf->env */
        if (err != 0) {
            printf("FATAL: cannot attach JACK thread to JVM\n");
            return;
        }
        env = inf->env;
        /* get handle to class ByteBuffer */
        inf->cls_ByteBuffer = (*env)->FindClass(env, CLASS_BYTEBUFFER);	
        if (inf->cls_ByteBuffer == NULL) {
            throwExc(env, "cannot access class java.nio.ByteBuffer");
            return 0;
        }
        /* get handle to class JJackSystem */
        inf->cls_JJackSystem = (*env)->FindClass(env, CLASS_JJACKSYSTEM);	
        if (inf->cls_JJackSystem == NULL) {
            throwExc(env, "cannot access class de.gulden.framework.jjack.JJackSystem");
            return 0;
        }
        /* get handle to method JJackSystem.process(...) */	
        inf->mid_process = (*env)->GetStaticMethodID(env, inf->cls_JJackSystem, METHOD_PROCESS, METHOD_PROCESS_SIG);
        if (inf->mid_process == NULL) {
            throwExc(env, "cannot access method JJackSystem.process(...)");
            return 0;
        }
    } else { /* has been attached to the JVM before, inf->env is valid */
        env = inf->env;
    }
    
    /* allocate DirectByteBuffer objects */
    for (mode=INPUT; mode<=OUTPUT; mode++) {
        byteBufferArray[mode] = (*env)->NewObjectArray(env, inf->portCount[mode], inf->cls_ByteBuffer, NULL);
        for (i=0; i < inf->portCount[mode]; i++) {
            byteBuffer = (*env)->NewDirectByteBuffer(env, (jack_default_audio_sample_t *) jack_port_get_buffer(inf->port[mode][i], nframes), nframes*4);
            (*env)->SetObjectArrayElement(env, byteBufferArray[mode], i, byteBuffer);
        }
    }

    /* callback to Java */
    (*env)->CallStaticVoidMethod(env, inf->cls_JJackSystem, inf->mid_process, byteBufferArray[INPUT], byteBufferArray[OUTPUT]);

    /* (no detach from JVM , thread remains attached): (*jvm)->DetachCurrentThread(jvm); */

    return 0;      
}


/* ------------------------------------------------------------------------ */
/* --- Implementations of natively declared Java methods                    */
/* ------------------------------------------------------------------------ */

/*
 * private static native void nativeInit() throws JJackException
 */
JNIEXPORT void JNICALL Java_de_gulden_framework_jjack_JJackSystem_nativeInit(JNIEnv *env, jclass cls) {
    INF inf;
    jstring namej;
    jint ports[MODES];
    const char *name;
    jack_client_t *client;
    int portCount;
    int portAutoconnect; /* boolean */
    char *portName;
    int mode, i;
	
    /* allocate memory for inf-structure */
    inf = (INF) malloc(sizeof(struct Inf));
	
    /* save pointer into java object */
    setStaticLongField(env, cls, FIELD_INFPOINTER, (jlong)inf);
	
    /* handle to JNI environment */
    inf->env = NULL;  /* initialize with NULL to mark that JACK thread needs to call AttachCurrentThread when first time inside process-callback-function */
	
    /* handle to JavaVM */
    (*env)->GetJavaVM(env, &inf->jvm);    

    /* register Jack client */
    namej = getStaticStringField(env, cls, FIELD_CLIENTNAME);
    name = allocchars(env, namej);
	
    printf("natively registering jack client \"%s\"\n", name);
	
    client = jack_client_new(name);
    freechars(env, namej, name);
    if (client == 0) {
        return throwExc(env, "cannot register jack client, jack server not running?");
    }
    inf->client = client;

    /* register ports */
    for (mode=INPUT; mode<=OUTPUT; mode++) {
        portCount = getStaticIntField(env, cls, FIELD_PORTCOUNT[mode]);
        portAutoconnect = getStaticBooleanField(env, cls, FIELD_PORTAUTOCONNECT[mode]);
        if (portCount > MAX_PORTS) { /* limit to max */
            portCount = MAX_PORTS;
        }
        inf->portCount[mode] = portCount;
        inf->portAutoconnect[mode] = portAutoconnect;
    }	
	
    printf("using %i input ports, %i output ports\n", inf->portCount[INPUT], inf->portCount[OUTPUT]);
	
    portName = malloc(100);
    for (mode=INPUT; mode<=OUTPUT; mode++) {
        for (i=0; i < inf->portCount[mode]; i++) {
            sprintf(portName, "%s_%i", MODE_LABEL[mode], (i+1));
            inf->port[mode][i] = jack_port_register(client, portName, JACK_DEFAULT_AUDIO_TYPE, MODE_JACK[mode], 0);
        }
    }
    free(portName);

    /* set callback functions */
    jack_set_process_callback(client, process, inf);
    /*jack_on_shutdown(client, callbackShutdown, callbackArg);*/
}

/*
 * private static native void nativeStart() throws JJackException
 */
JNIEXPORT void JNICALL Java_de_gulden_framework_jjack_JJackSystem_nativeStart(JNIEnv *env, jclass cls) {
    INF inf = getInf(env, cls);
    const char **ports;
    const char *msg[128];
    int min;
    int mode, i;

    /* activate jack client */
    if (jack_activate(inf->client)) {
        return throwExc(env, "cannot activate client");
    }

    /* autoconnect ports if requested (can't do this before the client is activated)*/
    for (mode=INPUT; mode<=OUTPUT; mode++) {
        if (inf->portAutoconnect[mode]) {
            printf("autoconnecting %s ports\n", MODE_LABEL[mode]);
            if ((ports = jack_get_ports(inf->client, NULL, NULL, JackPortIsPhysical|MODE_JACK[ 1 - mode ])) == NULL) { /* (1-mode as we connect outputs to inputs and inputs to outputs) */
                return throwExc2(env, "Cannot find any physical ports to autoconnect ", MODE_LABEL[mode]);
            }
            for (i=0; i < inf->portCount[mode]; i++) {
                printf("%s %i\n", MODE_LABEL[mode], (i+1));
                /* will fail if more ports are requested than available (as returned by jack_get_ports) */
                if ( mode == INPUT ) {
                    if (jack_connect(inf->client, ports[i], jack_port_name(inf->port[mode][i]))) {
                        return throwExc(env, "cannot autoconnect input port");
                    }
                } else { // mode == OUTPUT
                    if (jack_connect(inf->client, jack_port_name(inf->port[mode][i]), ports[i])) {
                        return throwExc(env, "cannot autoconnect output port");
                    }
                }
            }
            free (ports);
        }
    }
}

/*
 * private static native void nativeDestroy() throws JJackException
 */
JNIEXPORT void JNICALL Java_de_gulden_framework_jjack_JJackSystem_nativeDestroy(JNIEnv *env, jclass cls) {
    INF inf = getInf(env, cls);
    jack_client_close(inf->client);
    free(inf);
}

/*
 * public static native int getSampleRate()
 */
JNIEXPORT jint JNICALL Java_de_gulden_framework_jjack_JJackSystem_getSampleRate(JNIEnv *env, jclass cls) {
    INF inf = getInf(env, cls);
    return jack_get_sample_rate(inf->client);
}
