
package org.lyh.http.proxy;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String get4data(@QueryParam("sleep") @DefaultValue("1") Integer sleep) {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    @POST
    @Path("/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    public Object upload(@FormDataParam("file") InputStream fileInputStream,
                         @FormDataParam("file") FormDataContentDisposition fileMetaData){
        String UPLOAD_PATH = "/tmp/";

        File   outfile;
        String fileName = null;
        try
        {
            int read;
            byte[] bytes = new byte[4086 * 100];

            fileName = new String(fileMetaData.getFileName().getBytes("ISO8859-1"),"UTF-8");
            if(! System.getProperty("os.name")
                    .toLowerCase().startsWith("win")){
                System.setProperty("sun.jnu.encoding","utf-8");
            }
            outfile = new File(UPLOAD_PATH + fileName);
            OutputStream out = new FileOutputStream(outfile);
            while ((read = fileInputStream.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e)
        {
            /// throw new WebApplicationException("Error while uploading file. Please try again !!");
            e.printStackTrace();
        }
        Map<String,Object> data = new HashMap<>();
        data.put("filepath",fileName);
        return data;
    }

    @GET
    @Path("/download")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Object download(){
        File file = new File(hostsfile);
        if(file.exists()){
            Response.ResponseBuilder responseBuilder = Response.ok(file);
            responseBuilder.header("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
            return responseBuilder.build();
        }
        return null;
    }
}
