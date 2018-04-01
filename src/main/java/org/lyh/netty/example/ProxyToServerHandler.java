package org.lyh.netty.example;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/19 16:18
 */
public class ProxyToServerHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ProxyToServerHandler.class);

    private final ChannelHandlerContext clientContext;

    private FullHttpRequest request;


    public ProxyToServerHandler(ChannelHandlerContext ctx, FullHttpRequest request) {
        this.clientContext = ctx;
        this.request = request;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //logger.info("channelActive");
        try
        {

            ChannelFuture future = ctx.channel().writeAndFlush(request.retain());
            future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        } finally {
            request.release();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        //logger.info("channelRead0");
        response.headers().set("Custom-Header","xxxxx");
        //response.headers().set(RtspHeaderNames.CONTENT_TYPE,"application/json");
        clientContext.channel().writeAndFlush(response.retain()).addListener(ChannelFutureListener.CLOSE);
        //ctx.channel().closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught",cause);
        ctx.close();
        clientContext.close();
    }
}
