package org.lyh.http.proxy.exception;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/28 22:01
 */
public class IgnoreException extends StandardException {
    public IgnoreException(String msgCode) {
        super(msgCode);
    }
}
