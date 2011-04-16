#!/bin/sh

JAVA=java
MVN=mvn

WD=`pwd`
cd ecr-installer
if [ ! -f target/ecr-installer-*.jar ]; then
  ${MVN} install
fi
cd ${WD}

INSTALLER=ecr-installer/target/ecr-installer-*.jar

rm -rf target
mkdir target
${JAVA} -jar ${INSTALLER} -r ../repository/target/repository  target/ecr-default.zip

