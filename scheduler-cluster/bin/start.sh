#!/bin/bash

JAR_NAME='scheduler-cluster-1.0-SNAPSHOT.jar'
STARTED_FLAG='event loop started'
PID_FILE='PID'

RUN_MODE='debug'

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
CONF_DIR=$DEPLOY_DIR

LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=$LOGS_DIR/stdout.log

OPTION_ARGS=" /conf/config.properties"

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Dinit.run.mode=cluster-master"
JAVA_DEBUG_OPTS=""
if [ "$RUN_MODE" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 "
fi

JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xmx8G -Xms512m -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
    JAVA_MEM_OPTS=" -server -Xms512m -Xmx8G -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi
#CONFIG_FILES=" -Dlogging.path=$LOGS_DIR  -Dspring.config.location=$CONF_DIR/application.properties "
echo -e "Starting the $SERVER_NAME ..."
nohup /export/servers/jdk1.8.0_60/bin/java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -jar $DEPLOY_DIR/$JAR_NAME $OPTION_ARGS > $STDOUT_FILE 2>&1 &
PC=0
COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    PC=`ps -f | grep java | grep "$JAR_NAME" | awk '{print $2}' | wc -l`
    if [ $PC -lt 1 ]; then
            echo "start fail!"
            echo "`tail -n1000 $STDOUT_FILE`"
            exit -1
    fi

    COUNT=`less $STDOUT_FILE |grep "$STARTED_FLAG" |wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
done


echo "OK!"
PIDS=`ps -f | grep java | grep "$JAR_NAME" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
