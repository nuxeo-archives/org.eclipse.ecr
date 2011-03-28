#!/bin/bash
################################################################################
# Copyright (c) 2011 EclipseSource Inc. and others.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
#     hmalphettes - initial API and implementation
################################################################################
# This scripts generates a command-line to launch equinox.
# It uses the arguments defined in the *.ini file

# set path to eclipse folder. If local folder, use '.'; otherwise, use /path/to/eclipse/
eclipsehome=`dirname $0`;
cd $eclipsehome
eclipsehome=`pwd`

iniLookupFolder=$eclipsehome
# get path to equinox jar inside $eclipsehome folder
ini=$(find $eclipsehome -mindepth 1 -maxdepth 1 -name "*.ini" | sort | tail -1);
if [ ! -f "$ini" ]; then
  #maybe a mac
  appFolder=$(find $eclipsehome -mindepth 1 -maxdepth 1 -type d -name "*.app" | sort | tail -1);
  iniLookupFolder="$appFolder/Contents/MacOS"
  if [ -d "$iniLookupFolder" ]; then
    ini=$(find $iniLookupFolder -mindepth 1 -maxdepth 1 -type f -name "*.ini" | sort | tail -1);
  fi
fi
if [ -f "$ini" ]; then
  #skip the first 2 lines (--startup ...) and skip the -vmargs and everything that follows
  #args=`awk 'NR == 4,/^-vmargs/{print x};{x=$0}' $ini`
  #read the startup
  startup=`sed -n '/^-startup/{n;p;}' $ini`
  #consume the -startup line and its value which is the next line.
  args=`sed '/^-startup/,+1d' $ini`
  #remove the -vmargs and following lines.
  args=`echo "$args" | sed -n '/^-vmargs/,$!p'`
fi
if [ ! -f "$startup" ]; then
  #was returned as path relative to iniLookupFolder
  if [ ! -f "$iniLookupFolder/$startup" ]; then
    if [ -d "$eclipsehome/plugins" ]; then
      startup=$(find "$eclipsehome/plugins" -name "org.eclipse.equinox.launcher_*.jar" | sort | tail -1);
    fi
    if [ ! -f "$startup" ]; then
      echo "Can't locate the launcher jar $startup"
      exit 2
    fi
  else
    startup="$iniLookupFolder/$startup"
  fi
fi

##VM arguments and system properties
#PermGen
XXMaxPermSize=`echo "$args" | sed -n '/--launcher\.XXMaxPermSize/{n;p;}'`
if [ -n "$XXMaxPermSize" ]; then
  XXMaxPermSize="-XX:MaxPermSize=$XXMaxPermSize"
  #also remove those 2 lines from the args
  args=`echo "$args" | sed '/--launcher\.XXMaxPermSize/,+1d'`
fi
#vmargs
#VMARGS=`sed '1,/-vmargs/d' $ini | tr '\n' ' '`$XXMaxPermSize
VMARGS=`sed '1,/-vmargs/d' $ini`

if [ -z "$JAVA_OPTS" ]; then
  JAVA_OPTS=`echo "$VMARGS" | tr '\n' ' '`$XXMaxPermSize
  if [ -z "$JAVA_OPTS" ]; then
    JAVA_OPTS="-XX:MaxPermSize=384m -Xms96m -Xmx784m -XX:+HeapDumpOnOutOfMemoryError"
  fi
elif [ -n "$VMARGS" ]; then
  #need to merge the JAVA_OPTS and the vmargs defined in the ini file.
  #we don't pretend to do this perfectly. we just do it well enough for the most common options
  #the JAVA_OPTS have precedence over the vmargs
  VMARGS_UPDATED=""
  JAVA_OPTS=" $JAVA_OPTS "
  
  #for each line of the vmargs, see if there is a corresponding one in JAVA_OPTS.
  #if so remove it.
  for tok in $VMARGS; do
    #see if it is a parameter with a value: key=value
    if [ $(echo "$tok" | grep -c -F -e "=") -ne 0 ]; then
      key=`echo "$tok" | cut -d'=' -f1`"="
      #ok now look for this key in the JAVA_OPTS; if defined, then remove this line.
      if [ $(echo "$JAVA_OPTS" | grep -c -F -e " $key") -ne 0 ]; then
        echo "warn: JAVA_OPTS overrides $key defined in $ini"
      else
        VMARGS_UPDATED="$VMARGS_UPDATED $tok"
      fi
    elif [ $(echo " $tok" | grep -c -F -e " -Xms") -ne 0 ]; then #keep the space in " $tok"
      if [ $(echo "$JAVA_OPTS" | grep -c -F -e ' -Xms') -ne 0 ]; then
        echo "warn: JAVA_OPTS overrides -Xms defined in $ini"
      else
        VMARGS_UPDATED="$VMARGS_UPDATED $tok"
      fi
    elif [ $(echo " $tok" | grep -c -F -e " -Xmx") -ne 0 ]; then #keep the space in " $tok"
      if [ $(echo "$JAVA_OPTS" | grep -c -F -e " -Xmx") -ne 0 ]; then
        echo "warn: JAVA_OPTS overrides -Xmx defined in $ini"
      else
        VMARGS_UPDATED="$VMARGS_UPDATED $tok"
      fi
    else
      #consider this is a flag and look for the same flag in the JAVA_OPTS
      if [ $(echo "$JAVA_OPTS" | grep -c -F -e " $tok") -ne 0 ]; then
        echo "warn: JAVA_OPTS and $ini both define $tok"
      else
        VMARGS_UPDATED="$VMARGS_UPDATED $tok"
      fi
    fi
  done
  if [ -n "$XXMaxPermSize" -a $(echo "$JAVA_OPTS" | grep -c -F -e " -XX:MaxPermSize=") -ne 0 ]; then
    echo "warn: JAVA_OPTS overrides -XX:MaxPermSize= defined in $ini"
  else
    VMARGS_UPDATED="$VMARGS_UPDATED $XXMaxPermSize"
  fi
  JAVA_OPTS="$JAVA_OPTS $VMARGS_UPDATED"
  #echo "JAVA_OPTS MERGED $JAVA_OPTS"
fi

#use -install unless it was already specified in the ini file:
installArg=$(echo "$args" | sed '/^-install/!d')
if [ -n "$installArg" ]; then
    #installArg=`echo "$args" | sed -n '/-install/{n;p;}'`
    #leave the install as defined
    installArg=""
else
    installArg=" -install $eclipsehome"
fi

#use -configuration unless it was already specified in the ini file:
configurationArg=
if echo $* | grep -Eq ' -configuration'
then
    configurationArg=""
else
    tmp_config_area=`mktemp -d /tmp/cloudConfigArea.XXXXXX`
    configurationArg=" -configuration $tmp_config_area"
fi

#Read the console argument. It could be a flag.
#console=`awk '{if ($1 ~ /-console/){print $1}}' < $ini | head -1`
console=`echo "$args" | sed '/^-console/!d'`
if [ -n "$console" ]; then
  consoleArg=`echo "$args" | sed -n '/^-console/{n;p;}'`
  first=`echo "$consoleArg" | cut -c1-1`
  args=`echo "$args" | sed '/-console/,+1d'`
  if [ "$first" = "-" ]; then
    console=" -console"
  else
    console=" -console $consoleArg"
  fi
fi

args=`echo "$args" | tr '\n' ' '`

cmd="java $JAVA_OPTS -jar $startup $args$installArg$configurationArg$console $*"
echo "Staring Equinox with $cmd"
$cmd