#!/bin/sh

export BASEDIR=..
export CLASSPATH=$BASEDIR/conf

nohup java -server -classpath $CLASSPATH -Dbase.dir=$BASEDIR -jar $BASEDIR/lib/starter.jar  > $BASEDIR/logs/nohup.out &

tail -f $BASEDIR/logs/nohup.out
