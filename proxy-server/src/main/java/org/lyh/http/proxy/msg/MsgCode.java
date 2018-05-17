package org.lyh.http.proxy.msg;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/11 19:28
 */
public interface MsgCode {
    String E_SYS_ERR = "-1";

    String SUCCESS = "0";

    String E_ENTITY_NOT_FOUND = "4004";

    String E_HEAD_ATTACK = "5010";

    String E_THD_CONNECT_TIMEOUT = "7000";

    String E_THD_CONNECT_FAILED = "7001";

    String E_THD_SVR_INSIDE_CLOSED = "7002";
    String E_THD_READ_TIMEOUT = "7003";
}
