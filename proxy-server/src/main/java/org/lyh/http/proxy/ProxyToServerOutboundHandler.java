package org.lyh.http.proxy;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/18 22:42
 */
public class ProxyToServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ProxyToServerOutboundHandler.class);

    private final ChannelHandlerContext client2ProxyCtx;

    private FullHttpRequest request;

    public ProxyToServerOutboundHandler(ChannelHandlerContext client2ProxyCtx, FullHttpRequest request) {
        this.client2ProxyCtx = client2ProxyCtx;
        this.request = request;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.info("ProxyToServerOutboundHandler.connect");
        ctx.connect(remoteAddress,localAddress,promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(! channelFuture.isSuccess()){
                    ctx.fireExceptionCaught(channelFuture.cause());
                } else {
                    ctx.channel().writeAndFlush(request.retain());
                }
            }
        }));
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        logger.info("ProxyToServerOutboundHandler.write");
        ctx.write(msg,promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(! channelFuture.isSuccess()){
                    ctx.fireExceptionCaught(channelFuture.cause());
                }
            }
        }));
    }
}
