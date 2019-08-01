#!/bin/sh

THIS_DIR=`dirname $0`
CP=$THIS_DIR/*

java -cp "$CP" "-Djna.nosys=true" com.virtenio.commander.Commander "$@"
