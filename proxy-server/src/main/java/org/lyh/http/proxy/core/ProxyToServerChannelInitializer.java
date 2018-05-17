package org.lyh.http.proxy.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.lyh.http.proxy.conf.ProxyConstant;
import org.lyh.http.proxy.filter.DefaultProxyResponseFilter;
import org.lyh.http.proxy.filter.ProxyResponseFilter;

import java.util.concurrent.TimeUnit;

/**
 * 从代理平台到服务端的通道初始化类
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/27 17:48
 */
public class ProxyToServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    public static final String PROXY_TO_SERVER_OUTBOUND_HANDLER = "proxyToServerOutboundHandler";
    public static final String HTTP_CODEC = "http-codec";
    public static final String READ_TIMEOUT_HANDLER = "readTimeoutHandler";
    public static final String HTTP_AGGREGATOR = "http-aggregator";
    public static final String PROXY_TO_SERVER_INBOUND_HANDLER = "proxyToServerInboundHandler";
    public static final String GLOBAL_EXCEPTION_HANDLER = "globalExceptionHandler";

    private final ChannelHandlerContext client2ProxyCtx;

    private static ProxyResponseFilter responseFilter = new DefaultProxyResponseFilter();

    private FullHttpRequest request;

    public ProxyToServerChannelInitializer(ChannelHandlerContext client2ProxyCtx, FullHttpRequest request) {
        this.client2ProxyCtx = client2ProxyCtx;
        this.request = request;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        /*
        *  对客户端而言，先出站，后入站
        * */
        ProxyToServerOutboundHandler toServerOutboundHandler = new ProxyToServerOutboundHandler();
        ch.pipeline().addLast(PROXY_TO_SERVER_OUTBOUND_HANDLER,toServerOutboundHandler);
        ch.pipeline().addLast(HTTP_CODEC,new HttpClientCodec());
        ch.pipeline().addLast(READ_TIMEOUT_HANDLER, new ReadTimeoutHandler(ProxyConstant.READ_TIMEOUT, TimeUnit.MILLISECONDS));
        ch.pipeline().addLast(HTTP_AGGREGATOR,new HttpObjectAggregator(ProxyConstant.MAX_BODY_LENGTH));
        ProxyToServerInboundHandler toServerInboundHandler = new ProxyToServerInboundHandler(client2ProxyCtx, request);
        toServerInboundHandler.addFilter(responseFilter);
        ch.pipeline().addLast(PROXY_TO_SERVER_INBOUND_HANDLER,toServerInboundHandler);
        ch.pipeline().addLast(GLOBAL_EXCEPTION_HANDLER,new GlobalExceptionHandler(client2ProxyCtx.channel()));
    }
}
