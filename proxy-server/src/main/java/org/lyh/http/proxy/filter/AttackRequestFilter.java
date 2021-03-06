package org.lyh.http.proxy.filter;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.lyh.http.proxy.msg.MsgCode;
import org.lyh.http.proxy.exception.StandardException;

/**
 * 疑似攻击请求过滤
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/15 13:16
 */
public class AttackRequestFilter implements ProxyRequestFilter {

    public static final String HTTP_SPL = "%0D%0A".toLowerCase();

    @Override
    public FullHttpRequest filter(FullHttpRequest request) {
        checkHeaders(request.headers());
        return request;
    }

    /*
     * 过滤可能的头攻击
     */
    private void checkHeaders(HttpHeaders headers) {
        headers.forEach(header -> {
            if(header.getKey().toLowerCase().contains(HTTP_SPL) || header.getValue().toLowerCase().contains(HTTP_SPL)){
                throw new StandardException(MsgCode.E_HEAD_ATTACK);
            }
        });
    }
}
