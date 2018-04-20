package org.lyh.http.proxy.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/14 10:14
 */
public class DefaultProxyResponseFilter implements ProxyResponseFilter{

    private static final Logger logger = LoggerFactory.getLogger(DefaultProxyResponseFilter.class);

    @Override
    public FullHttpResponse filter(FullHttpRequest request, FullHttpResponse response) {
        logger.info("{} {} {}",request.uri(),request.method().name(),response.status().code());
        return response;
    }
}
