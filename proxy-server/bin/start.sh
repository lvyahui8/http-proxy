#!/usr/bin/env bash

current_path=$(dirname $(which $0))

cd "$current_path/.."

mainClass=org.lyh.http.proxy.HttpProxyServer

nohup java -Dfile.encoding=utf8 -Djava.ext.dirs=./lib -classpath ./conf  \
    -Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector \
    ${mainClass} > /dev/null 2>&1 &

processId=$(ps axu | grep ${mainClass} | grep -v grep | awk '{print $2}' )

if [ "$processId" != "" ]; then
    echo "started proxy server [$processId]"
fi


