#!/usr/bin/env bash

current_path=$(dirname $(which $0))

processId=$(ps axu | grep org.lyh.http.proxy.HttpProxyServer | grep -v grep | awk '{print $2}' )
if [ "$processId" != "" ]; then
    echo "stop proxy server [$processId]"
    kill -9 $processId
fi
