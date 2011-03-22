#!/bin/sh

WD=`pwd`
cd ../repository/target/repository/
TARGET=`pwd`
cd "${WD}"

javac GenProduct.java
java -cp . GenProduct "${TARGET}" 

rm GenProduct.class

cp run.sh.template "${TARGET}/run.sh"

