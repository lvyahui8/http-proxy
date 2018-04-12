
package org.lyh.http.proxy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;

@Path("/test")
public class TestResource {

    // TODO: update the class to suit your needs
    
    @GET @Path("/get4data")
    public String get4data() {
        return "Got it!";
    }

    @GET @Path("/get")
    public Object get(){
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("1");
        strings.add("2");
        return strings;
    }


}
