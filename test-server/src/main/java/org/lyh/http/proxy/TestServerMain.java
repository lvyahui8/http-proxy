
package org.lyh.http.proxy;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.threadpool.GrizzlyExecutorService;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;


public class TestServerMain {

    private static int getPort(int defaultPort) {
        String httpPort = System.getProperty("jersey.test.port");
        if (null != httpPort) {
            try {
                return Integer.parseInt(httpPort);
            } catch (NumberFormatException e) {
            }
        }
        return defaultPort;
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost/").port(getPort(10024)).build();
    }

    public static final URI BASE_URI = getBaseURI();
    
    protected static HttpServer startServer() throws IOException {
        ResourceConfig resourceConfig = new PackagesResourceConfig("org.lyh.http.proxy");
        resourceConfig.getContainerResponseFilters().add(new IOFilter());
        resourceConfig.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING , Boolean.TRUE);
        HttpServer httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI, resourceConfig);
        ThreadPoolConfig threadConfig = ThreadPoolConfig.defaultConfig().setPoolName("test-server-worker-thread")
                .setCorePoolSize(10)
                .setMaxPoolSize(10);
        GrizzlyExecutorService workerThreadPool = (GrizzlyExecutorService ) httpServer.getListeners()
                .iterator().next().getTransport().getWorkerThreadPool();
        workerThreadPool.reconfigure(threadConfig);
        return httpServer;
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer httpServer = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl",
                BASE_URI));
        Thread.currentThread().join();
    }    
}
