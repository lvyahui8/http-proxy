package org.lyh.http.proxy.msg;


import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/11 19:34
 */
public class MsgTranslater {
    private static final String MSG_FILE = "msg.properties";
    private static final PropertiesConfiguration MSG ;
    private static final String DEFAULT_MSG = "unkown";

    static  {
        MSG = loadProperties();
    }

    private static PropertiesConfiguration loadProperties() {
        PropertiesConfiguration configuration ;
        try {
            configuration = new PropertiesConfiguration(MSG_FILE);
            configuration.setReloadingStrategy(new FileChangedReloadingStrategy());
        } catch (Throwable e) {
            // 不处理
            configuration = new PropertiesConfiguration();
        }
        return configuration;
    }

    public static String getMsg(String msgCode){
        return MSG.containsKey(msgCode) ? MSG.getString(msgCode) : DEFAULT_MSG;
    }

}
