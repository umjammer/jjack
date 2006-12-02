-----------------------------------------------------------------------------
JJack 0.2.1 - Jack-to-Java API
Copyright (c) Jens Gulden, mail@jensgulden.de
Distributed under the GNU Lesser General Public License (LGPL).
This software comes with NO WARRANTY. See file License.txt for details.
-----------------------------------------------------------------------------

Installation
------------
Unpack the distribution archive jjack-0_2_1.tgz to /usr/java. A directory 
/usr/java/jjack-0_2_1 will be created. You can also choose a different
directory, in that case the absolute path values in the shell-scripts 
bin/jjack.sh and bin/runBeanBuilder.sh need to be adopted to your choice.

The native bridge library libjjack.so (on Linux systems) needs to be 
accessible by the Java Virtual Machine (JVM).
There are two ways to make sure the JVM can load the native bridge library:
- Copy the file <jjack-install-dir>/lib/<arch>/<os>/* to the system's default
  library path. On Linux:
  cp ./lib/i386/linux/libjjack.so /usr/lib/
  Alternatively create a filesystem-link to the binary file at the system's
  default library location:
  cd /usr/lib
  ln -s <jjack-install-dir>/lib/i386/linux/libjjack.so
- Or the library-path can directly be set through the JVM system property
  "java.library.path". Add the parameter
  "-Djava.library.path=<jjack-install-dir>/lib/i386/linux" to the invocation
  command.

Note: The current version includes a pre-compiled binary library for 
i386-Linux. To run JJack on different platforms, the source files libjjack.c 
and libjjack.h need to be compiled on your system. Please send a copy of the 
binary and a description of your compilation process if you succeed in running 
JJack on different platforms.

Before using JJack, don't forget to start the JACK daemon. 
This is either done by manually invoking jackd, or by configuring the system
to start up jackd at boot time. There are several alternative ways to configure
and run the daemon, see the JACK user documentation for details
(http://www.djcj.org/LAU/jack/).

Tested on
---------
- Debian 3.0 unstable, kernel 2.4.24, jackd 0.94.0. libjack-0.80.0.so.0.0.23,
  ALSA 1.0.2, Sun-J2SE SDK 1.4.2_03, both running jackd as root with
  realtime-support, and as ordinary user without realtime-support.
- Debian 3.0 testing, kernel 2.6.6, jackd 0.98.1, libjack-0.80.0.so.0.0.23,
  ALSA 1.0.4rc2, Sun-J2SE SDK 1.4.2_04, running jackd as normal user without
  realtime-capabilities.
- Agnula DeMuDi 1.2, kernel 2.4.25 with multimedia/realtime patches, 
  jackd 0.94.0, libjack-0.80.0.so.0.0.23, ALSA 1.0.6a, Sun-J2SE JRE 1.5.0,
  running jackd as normal user without realtime-capabilities.

History
-------
Version 0.2.1, 2006-12-02:
* streamlining of the build-process and completion of files in CVS:
- distinguishing between project-build and distribution-build
- inclusion of directories make, bin and doc into CVS
Now all files needed to perform a full project-build are available via CVS, 
including the ANT project-build script. The source codes have not changed
since version 0.2.

Version 0.2, 2004-11-16:
* Most changes apply to libjjack.c, mainly the use of AttachCurrentThread() to
  enable Java-callbacks by the JACK thread. This implies:
- no extra thread needed any longer to interact with the JACK thread,
  class JJackSystemThread has become obsolete and has been removed.
- the pthreads-library is no longer needed by the native implementation, as no
  more semaphores are required for thread synchronization.
* Some changes to class de.gulden.framework.jjack.JJackSystem, initialization
  will no longer start an internal processing thread.
* Some enhancements to this readme-file.

Version 0.1, 2004-05-25:
Initial release.

(list of files)