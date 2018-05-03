package org.lyh.http.proxy.core;

import org.lyh.http.proxy.bean.AccessLog;

import java.nio.channels.Channel;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/5/3 21:31
 */
public class ChannelAttribute {

    public static String initLogId(Channel clientChannel){
        return null;
    }

    public static String getLogId(Channel clientChannel){
        return null;
    }

    public static AccessLog initAccessLog(Channel clientChannel,HttpProxyEntity proxyEntity){
        return null;
    }

    public static AccessLog getAccessLog(Channel clientChannel){
        return null;
    }
}

