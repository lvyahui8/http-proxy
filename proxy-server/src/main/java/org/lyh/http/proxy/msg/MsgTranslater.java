package org.lyh.http.proxy.msg;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/11 19:34
 */
public class MsgTranslater {
    private static final String MSG_FILE = "msg.properties";
    private static final Properties MSG ;
    private static final String DEFAULT_MSG = "unkown";

    static  {
        MSG = loadProperties(MSG_FILE);
    }

    private static Properties loadProperties(String msgFile) {
        Properties properties = new Properties();
        try {
            InputStream resourceAsStream = null;
            resourceAsStream = MsgTranslater.class.getClassLoader().getResourceAsStream(MSG_FILE);

            try (InputStreamReader input = new InputStreamReader(resourceAsStream, "UTF-8")) {
                properties.load(input);
            }
        } catch (Throwable e) {
            // 不处理
        }
        return properties;
    }

    public static String getMsg(String msgCode){
        return MSG.containsKey(msgCode) ? MSG.getProperty(msgCode) : DEFAULT_MSG;
    }

}
