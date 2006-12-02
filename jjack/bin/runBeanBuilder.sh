#! /bin/sh
#
# Shell script to run the Bean Builder Application on 1.4 (and beyond)
# together with JJack support and with default JJack bean palette.
# Originally taken and modified from <beanbuilder>/run.sh.
#

### Please adopt these paths to your configuration: ###
BB_HOME=/usr/java/beanbuilder-1_0-beta
JJACK_HOME=/usr/java/jjack-0_2
SYSTEM=i386/linux
# (other systems than i386/linux need manual compilation of libjjack.c)
##############################################

CP=${JAVA_HOME}/lib/dt.jar:${BB_HOME}/builder.jar:${BB_HOME}/lib/jlfgr-1_0.jar:${JJACK_HOME}/lib/jjack.jar:${JJACK_HOME}/lib/jjack-clients.jar
LP=${JJACK_HOME}/lib/${SYSTEM}
${JAVA_HOME}/bin/java -cp ${CP} -Djava.library.path=${LP} JJackSystem Main -p ${JJACK_HOME}/bin/palette-jjack.xml $1 $2 $3 $4 $5
