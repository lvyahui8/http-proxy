#!/usr/bin/env bash

DOC_PATH=$(dirname $(which $0))
MAIN_CLASS="org.lyh.http.proxy.TestServerMain"

get_pid(){
    echo $(jps -lv | grep ${MAIN_CLASS} | awk '{print $1}')
}

start_server(){
    nohup java -Djava.ext.dirs=${DOC_PATH}/lib \
        ${MAIN_CLASS} >/dev/null 2>&1 &
    pid=$(get_pid)
    if [ "$pid" != "" ]; then
        echo "test-server startd[$pid]"
    fi
}

stop_server(){
    pid=$(get_pid)
    if [ "$pid" != "" ]; then
        kill -i ${pid}
        echo "test-server stoped[$pid]"
    fi
}

case $1 in
    start|s)
        start_server
        ;;
    stop)
        stop_server
        ;;
    restart|r)
        stop_server
        start_server
        ;;
    stat)
        ;;
     *)
        echo "Usage $0 start|stop|restart|stat"
        ;;
esac