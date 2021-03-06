package org.lyh.http.proxy.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/14 10:12
 */
public interface ProxyResponseFilter {
    FullHttpResponse filter(FullHttpRequest request,FullHttpResponse response);
}
