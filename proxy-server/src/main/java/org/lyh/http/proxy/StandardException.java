package org.lyh.http.proxy;

/**
 * 定义全局异常类
 * 代码中遇到不可执行的情况，统一抛出此异常
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/11 19:20
 */
public class StandardException extends RuntimeException {

    private final String msgCode;

    public StandardException(String msgCode) {
        this.msgCode = msgCode;
    }

    public String getMsgCode() {
        return msgCode;
    }
}
