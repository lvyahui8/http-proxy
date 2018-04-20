package org.lyh.http.proxy.core;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.lyh.http.proxy.msg.MsgCode;
import org.lyh.http.proxy.bean.StandardException;
import org.lyh.http.proxy.msg.StandardResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ConnectException;
import java.net.NoRouteToHostException;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/2 21:38
 */
public class GlobalExceptionHandler extends ChannelHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final Gson gson = new Gson();

    public static final String DEFAULT_CONTENT_TYPE = HttpHeaderValues.APPLICATION_JSON + "; charset=UTF-8";

    private Channel clientChannel;

    public GlobalExceptionHandler(Channel clientChannel) {
        this.clientChannel = clientChannel;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.info("Remote address: {}",ctx.channel().remoteAddress());
        logger.error("Exception :",cause);
        if(!(cause instanceof Error)){
            StandardResponse standardResponse = buildResponse(cause);
            writeToClienChannel(clientChannel, standardResponse);
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
        else if(cause instanceof ConnectTimeoutException){
            standardResponse.setCode(MsgCode.E_THD_CONNECT_TIMEOUT);
        }
        else if(cause instanceof ConnectException || cause instanceof NoRouteToHostException) {
            standardResponse.setCode(MsgCode.E_THD_CONNECT_FAILED);
        }
        else {
            standardResponse.setCode(MsgCode.E_SYS_ERR);
        }
        return standardResponse;
    }
}
