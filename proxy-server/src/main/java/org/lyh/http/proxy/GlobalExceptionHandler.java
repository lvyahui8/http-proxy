package org.lyh.http.proxy;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/2 21:38
 */
public class GlobalExceptionHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Gson gson = new Gson();

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.debug("remote address: {}",ctx.channel().remoteAddress());
        logger.error("Exception :",cause);
        if(!(cause instanceof Error)){
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK);
            response.headers()
                    .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                    .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            StandardResponse standardResponse = new StandardResponse();
            if(cause instanceof RuntimeException){
                standardResponse.setCode(MsgCode.E_SYS_ERR);
            }

            ByteBuf respBuf = Unpooled.copiedBuffer(gson.toJson(standardResponse).getBytes());

            response.headers().set(HttpHeaderNames.CONTENT_LENGTH,respBuf.readableBytes());
            response.replace(respBuf);

            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.close();
        }

    }
}
