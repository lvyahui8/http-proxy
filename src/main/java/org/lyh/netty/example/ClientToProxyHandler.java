package org.lyh.netty.example;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/1/31 10:43
 */
public class ClientToProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ClientToProxyHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       //logger.info("channelActive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught",cause);
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
       // logger.info( "channelReadComplete");
        //ctx.flush();
    }

    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        //logger.info("channelRead0");
        Bootstrap bootstrap = new Bootstrap();
        msg.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        bootstrap.group(ctx.channel().eventLoop())
                .channel(HttpProxyServer.isWindows ? NioSocketChannel.class : EpollSocketChannel.class)
                .handler(new ProxyToServerChannelInitializer(ctx,msg.copy()));

        bootstrap.connect("127.0.0.1", 10024);
    }
}
