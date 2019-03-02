#!/bin/bash
JAR_NAME='scheduler-ui.jar'
PIDS=`ps aux | grep java | grep "$JAR_NAME" | awk '{print $2}'`

echo "PID: $PIDS"

for PID in $PIDS ; do
    kill -9 $PID > /dev/null 2>&1
done