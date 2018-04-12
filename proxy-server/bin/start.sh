#!/usr/bin/env bash

current_path=$(dirname $(which $0))

cd "$current_path/.."

nohup java -Dfile.encoding=utf8 -Djava.ext.dirs=./lib -classpath \
    ./conf org.lyh.http.proxy.HttpProxyServer > /dev/null 2>&1 &

processId=$(ps axu | grep org.lyh.http.proxy.HttpProxyServer | grep -v grep | awk '{print $2}' )

if [ "$processId" != "" ]; then
    echo "started proxy server [$processId]"
fi


