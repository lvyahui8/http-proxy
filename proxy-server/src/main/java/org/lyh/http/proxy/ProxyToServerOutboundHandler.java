package org.lyh.http.proxy;

import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/18 22:42
 */
public class ProxyToServerOutboundHandler extends ChannelOutboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ProxyToServerOutboundHandler.class);

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress,localAddress,promise.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(! channelFuture.isSuccess()){
                    logger.error("Connect failed, remote address : {}", remoteAddress);
                    ctx.fireExceptionCaught(channelFuture.cause());
                } else {
                     /* 可以在此时发出写请求，也可以在ProxyToServerInboundHandler@channelActive 时发送请求*/
                    //ctx.channel().writeAndFlush(request.retain());
                }
            }
        }));
    }


}
