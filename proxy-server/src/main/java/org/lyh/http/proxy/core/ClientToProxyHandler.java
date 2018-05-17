package org.lyh.http.proxy.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.lyh.http.proxy.*;
import org.lyh.http.proxy.exception.StandardException;
import org.lyh.http.proxy.filter.ProxyRequestFilter;
import org.lyh.http.proxy.msg.MsgCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * ClientToProxyHandler 只要处理客户端到proxy的入站事件，并且触发proxy到第三方server的出站操作即可
 *
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/1/31 10:43
 */
public class ClientToProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ClientToProxyHandler.class);
    private static final String HTTP_PROTOCOL = "http://";

    public static final int DEFAULT_CONNECT_TIMEOUT = 2000;

    private static EntitysManager entitysManager =  EntitysManager.getInstance();

    private  Bootstrap proxy2ServerBootstrap;

    private List<ProxyRequestFilter> requestFilters;

    public ClientToProxyHandler() {
        proxy2ServerBootstrap = new Bootstrap();

        proxy2ServerBootstrap.group(EventLoopGroupMannager.getWorkerGroup())
                .channel(HttpProxyServer.isWindows ? NioSocketChannel.class : EpollSocketChannel.class);
        proxy2ServerBootstrap.option(ChannelOption.TCP_NODELAY,true);

        requestFilters  = new ArrayList<>();
    }

    public ClientToProxyHandler addFilter(ProxyRequestFilter requestFilter){
        this.requestFilters.add(requestFilter);
        return this;
    }

    protected void channelRead0(ChannelHandlerContext clientChannelCtx, FullHttpRequest clientRequestMsg) throws Exception {
        URI uri = new URI(clientRequestMsg.uri());
        HttpProxyEntity entity = entitysManager.getEntity(uri.getPath(),clientRequestMsg.method().name());
        if(entity != null && entity.getTargetUri() != null
                && entity.getTargetUri().trim().length() > 0){

            for (ProxyRequestFilter proxyRequestFilter : requestFilters){
                clientRequestMsg = proxyRequestFilter.filter(clientRequestMsg);
            }

            /* 找到可代理的对象 */
            URL targetUrl = new URL(entity.getTargetUri().startsWith("http://")
                    ? entity.getTargetUri() : HTTP_PROTOCOL + entity.getTargetUri());

            clientRequestMsg.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            clientRequestMsg.headers().set(HttpHeaderNames.HOST,
                    targetUrl.getPort() < 0 ? targetUrl.getHost() : targetUrl.getHost() + ":" + targetUrl.getPort() );
            clientRequestMsg.setUri(targetUrl.getPath());

            proxy2ServerBootstrap.handler(new ProxyToServerChannelInitializer(clientChannelCtx,clientRequestMsg.copy()));
            proxy2ServerBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,DEFAULT_CONNECT_TIMEOUT);
            int svrPort = targetUrl.getPort() < 0 ? 80 : targetUrl.getPort();
            if (logger.isDebugEnabled()){
                logger.debug("connect to {}:{}",targetUrl.getHost(),svrPort);
            }
            /* 触发客户端的出站操作，依次是connect -> write -> read -> close */
            proxy2ServerBootstrap.connect(targetUrl.getHost(), svrPort);
        } else {
            throw new StandardException(MsgCode.E_ENTITY_NOT_FOUND);
        }
    }



}
