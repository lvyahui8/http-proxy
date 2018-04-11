package org.lyh.http.proxy;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/29 17:27
 */
public class EntitysManager {

    public static final Logger logger = LoggerFactory.getLogger(EntitysManager.class);

    public static final String ENTITYS_FILE = ClassLoader.getSystemResource("entitys.json").getFile();
    private static final String KEY_SP = ":";

    static {
        getInstance().startWatchThread();
    }

    private Map<String,HttpProxyEntity> httpProxyMap;

    private long lastTime ;

    private boolean watch = true;

    private Thread watchThread ;

    private EntitysManager() {

    }

    public void startWatchThread() {
        if(watchThread != null){
            return;
        }
        watchThread = new Thread(() -> {
            while(watch){
                File file = new File(ENTITYS_FILE);
                if(! file.exists()){
                    logger.error("file not found");
                    continue;
                }

                if(file.lastModified() > lastTime){
                    try {
                        loadFromEntitysJsonFile(new FileInputStream(ENTITYS_FILE));
                    } catch (FileNotFoundException e) {
                        logger.warn("file not found");
                        continue;
                    }
                    lastTime =  file.lastModified();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.warn("sleep err",e);
                }
            }
        });

        watchThread.start();
    }

    public void stopWatchThread(){
        setWatch(false);
        watchThread = null;
    }

    private static EntitysManager instance;

    public static EntitysManager getInstance(){
        if(instance == null){
            instance = new EntitysManager();
        }
        return instance;
    }

    private void loadFromEntitysJsonFile(InputStream stream){
        String json  = null;
        try {
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(stream,"UTF-8"));
            String line;
            json = "";
            while((line = reader.readLine()) != null){
                json += line;
            }
        } catch (IOException e) {
            logger.error("entitys file : {}",stream);
            logger.error("load entitys failed.",e);
        }
        loadFromEntitysJson(json);
    }

    private void loadFromEntitysJson(String entitysJson){
        Gson gson = new Gson();
        JsonArray items = gson.fromJson(entitysJson,JsonArray.class);
        httpProxyMap = new HashMap<>(items.size());
        for(JsonElement e : items){
            HttpProxyEntity httpProxyEntity = gson.fromJson(e, HttpProxyEntity.class);
            httpProxyMap.put(
                    (httpProxyEntity.getInboundPath() + KEY_SP + httpProxyEntity.getMethod())
                            .toLowerCase()
                    ,httpProxyEntity);
        }
    }

    private void setWatch(boolean watch) {
        this.watch = watch;
    }


    public HttpProxyEntity getEntity(String inboundPath,String method){
        return httpProxyMap != null ?
                httpProxyMap.get((inboundPath + KEY_SP + method).toLowerCase()) : null;
    }

}
