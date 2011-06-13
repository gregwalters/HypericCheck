#!/bin/bash

CLASSPATH=`ls lib/jars/* | xargs echo | sed 's/ /:/g'`
java -Djava.library.path=lib/pdk/lib -cp $CLASSPATH HypericCheck $@ | grep -v ":.*=.*,"
