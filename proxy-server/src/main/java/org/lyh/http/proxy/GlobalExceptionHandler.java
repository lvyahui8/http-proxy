package org.lyh.http.proxy;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
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

    public static final String DEFAULT_CONTENT_TYPE = HttpHeaderValues.APPLICATION_JSON + "; charset=UTF-8";

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("Remote address: {}",ctx.channel().remoteAddress());
        logger.error("Exception :",cause);
        if(!(cause instanceof Error)){
            StandardResponse standardResponse = buildResponse(cause);
            writeToClienChannel(ctx.channel(), standardResponse);
        } else {
            ctx.close();
        }

    }

    private void writeToClienChannel(Channel channel, StandardResponse standardResponse) {
        ByteBuf respBuf = Unpooled.copiedBuffer(gson.toJson(standardResponse).getBytes());

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.OK,respBuf);
        response.headers()
                .set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE)
                .set(HttpHeaderNames.CONTENT_TYPE, DEFAULT_CONTENT_TYPE);

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH,respBuf.readableBytes());

        channel.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private StandardResponse buildResponse(Throwable cause) {
        StandardResponse standardResponse = new StandardResponse();

        if (cause instanceof StandardException){
            StandardException standardException = (StandardException) cause;
            standardResponse.setCode(standardException.getMsgCode());
        }
        else {
            standardResponse.setCode(MsgCode.E_SYS_ERR);
        }
        return standardResponse;
    }
}
