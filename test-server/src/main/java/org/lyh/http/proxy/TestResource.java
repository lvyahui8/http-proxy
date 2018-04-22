
package org.lyh.http.proxy;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/test")
public class TestResource {

    private static final String hostsfile;

    private static final String WINDOWS_STATIC_HOSTS_FILE = "C:\\Windows\\System32\\drivers\\etc\\hosts";

    private static final String Linux_STATIC_HOST_FILE = "/etc/hosts";

    static {
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");

        if(isWindows){
            hostsfile = WINDOWS_STATIC_HOSTS_FILE;
        } else {
            hostsfile = Linux_STATIC_HOST_FILE;
        }

    }

    @GET @Path("/get4data")
    public String get4data() {
        return "Got it!";
    }

    @GET @Path("/get")
    public Object get(){
        List<String> hosts = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(hostsfile));
            String line ;
            while((line = br.readLine()) != null){
                if(line.trim().length() == 0 || line.trim().startsWith("#")){
                   continue;
                }
                hosts.add(line);
            }
        } catch (IOException e) {
            //
        } finally {
            if (br != null){
                try {
                    br.close();
                } catch (IOException e) {
                    //
                }
            }
        }
        return hosts;
    }


    @POST @Path("/post")
    public Object post(){
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("1");
        strings.add("2");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return strings;
    }
}
