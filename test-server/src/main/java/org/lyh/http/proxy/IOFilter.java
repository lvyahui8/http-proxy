package org.lyh.http.proxy;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.File;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/12 21:12
 */
public class IOFilter implements ContainerRequestFilter,ContainerResponseFilter {

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest) {
        System.out.println(containerRequest);
        return containerRequest;
    }

    @Override
    public ContainerResponse filter(ContainerRequest containerRequest, ContainerResponse containerResponse) {
        Object entity = containerResponse.getEntity();
        if(!(entity instanceof File)){
            containerResponse.getHttpHeaders().putSingle(HttpHeaders.CONTENT_TYPE,MediaType.APPLICATION_JSON);
            if(!(entity instanceof Answer)){
                Answer answer = new Answer();
                answer.setCode(0);
                answer.setMsg("");
                answer.setData(entity);
                containerResponse.setEntity(answer);
            }
        }

        return containerResponse;
    }
}
