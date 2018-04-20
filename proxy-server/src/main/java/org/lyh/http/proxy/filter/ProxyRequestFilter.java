package org.lyh.http.proxy.filter;

import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/14 10:10
 */
public interface ProxyRequestFilter {
    FullHttpRequest filter(FullHttpRequest request);
}
