package org.lyh.http.proxy;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/11 19:28
 */
public interface MsgCode {
    String E_SYS_ERR = "-1";

    String SUCCESS = "0";

    String E_HEAD_ATTACK = "5010";

    String E_THD_TIMEOUT = "7000";
    String E_ENTITY_NOT_FOUND = "4004";
}
