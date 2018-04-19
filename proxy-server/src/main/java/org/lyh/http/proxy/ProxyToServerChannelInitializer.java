package org.lyh.http.proxy;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * 从代理平台到服务端的通道初始化类
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/27 17:48
 */
public class ProxyToServerChannelInitializer extends ChannelInitializer<SocketChannel> {


    private final ChannelHandlerContext ctx;

    private static ProxyResponseFilter responseFilter = new DefaultProxyResponseFilter();

    private FullHttpRequest request;

    public ProxyToServerChannelInitializer(ChannelHandlerContext ctx, FullHttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        /*
        *  对客户端而言，先出站，后入站
        * */
        ProxyToServerOutboundHandler toServerOutboundHandler = new ProxyToServerOutboundHandler();
        ch.pipeline().addLast("proxyToServerOutboundHandler",toServerOutboundHandler);
        ch.pipeline().addLast("http-codec",new HttpClientCodec());
        ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(1024 * 1024));
        ProxyToServerInboundHandler toServerInboundHandler = new ProxyToServerInboundHandler(ctx, request);
        toServerInboundHandler.addFilter(responseFilter);
        ch.pipeline().addLast("proxyToServerInboundHandler",toServerInboundHandler);
        ch.pipeline().addLast("globalExceptionHandler",new GlobalExceptionHandler(ctx.channel()));
    }
}
