#!/bin/sh

WD=`pwd`

#the Java binary
JAVA="java"

# the class path
# JAVA_CP=plugins/org.eclipse.equinox.launcher_*.jar
JAVA_CP="plugins/${launcher.bundle}"

# the Java options
JAVA_OPTS="-Decr.home.dir=${WD}/ecr -Declipse.ignoreApp=true -Dosgi.noShutdown=true"

# uncomment this to enable debug
#JAVA_OPTS="${JAVA_OPTS} -Xdebug -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

# The launcher arguments
DIRS="-configuration file:configuration/ -install file:./"
ARGS="-arch x86_64 -nl en_US -consoleLog"

$JAVA $JAVA_OPTS -classpath $JAVA_CP org.eclipse.equinox.launcher.Main $DIRS $ARGS $*



