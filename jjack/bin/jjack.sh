#! /bin/sh
#
# Shell script to run the JJack Shell Application.
#

### Please adopt these paths to your configuration: ###
JJACK_HOME=/usr/java/jjack-0_2
SYSTEM=i386/linux
# (other systems than i386/linux need manual compilation of libjjack.c)
##############################################

CP=${JAVA_HOME}/lib/dt.jar:${JJACK_HOME}/lib/jjack.jar:${JJACK_HOME}/lib/jjack-clients.jar
LP=${JJACK_HOME}/lib/${SYSTEM}
${JAVA_HOME}/bin/java -cp ${CP} -Djava.library.path=${LP} JJack $1 $2 $3 $4 $5
