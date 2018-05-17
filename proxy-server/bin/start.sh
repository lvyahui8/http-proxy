#!/usr/bin/env bash

current_path=$(dirname $(which $0))

cd "$current_path/.."

mainClass=org.lyh.http.proxy.HttpProxyServer
# vmOptions="-server -XX:+UseG1GC -Xmx=1024m -Xms=256m io.netty.leakDetectionLevel=DISABLED"
vmOptions="-server -XX:+UseG1GC -Xmx1024m -Xms256m -Xss4m -Dio.netty.leakDetectionLevel=PARANOID"

if [ "X$1" != "Xjmx" ]; then
    nohup java ${vmOptions} -Dfile.encoding=utf8 -Djava.ext.dirs=./lib -classpath ./conf  \
        -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector \
        ${mainClass} > /dev/null 2>&1 &
else
    nohup java ${vmOptions} -Dfile.encoding=utf8 -Djava.ext.dirs=./lib -classpath ./conf  \
        -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector \
        -Dcom.sun.management.jmxremote.authenticate=false \
        -Dcom.sun.management.jmxremote.ssl=false \
        -Dcom.sun.management.jmxremote.port=9001 \
        -Djava.rmi.server.hostname=10.125.50.32 \
        ${mainClass} > /dev/null 2>&1 &
fi

processId=$(ps axu | grep ${mainClass} | grep -v grep | awk '{print $2}' )

if [ "$processId" != "" ]; then
    echo "started proxy server [$processId]"
fi


