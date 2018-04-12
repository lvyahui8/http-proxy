package org.lyh.http.proxy;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/2 21:38
 */
public class GlobalExceptionHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("Local address: {},remote address: {}",ctx.channel().localAddress(),ctx.channel().remoteAddress());
        logger.error("Exception :",cause);
        ctx.close();
    }
}
