package org.lyh.http.proxy.core;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.lyh.http.proxy.exception.IgnoreException;
import org.lyh.http.proxy.filter.ProxyResponseFilter;
import org.lyh.http.proxy.msg.MsgCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/19 16:18
 */
public class ProxyToServerInboundHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ProxyToServerInboundHandler.class);
    private static final String REMOTE_STATUS = "Remote-Status";

    private final ChannelHandlerContext client2ProxyCtx;

    private FullHttpRequest request;

    private List<ProxyResponseFilter> responseFilters ;

    private boolean sended = false;
    private boolean recved = false;

    public ProxyToServerInboundHandler(ChannelHandlerContext client2ProxyCtx, FullHttpRequest request) {
        this.client2ProxyCtx = client2ProxyCtx;
        this.request = request;
        this.responseFilters = new ArrayList<>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(logger.isDebugEnabled()){
            logger.debug("uri: {}",request.uri());
            logger.debug("modified request headers: {}",request.headers());
            logger.debug("modified request body: {}",request.content().toString(Charset.defaultCharset()));
        }

        /*
        * 与服务端的连接channel以建立并初始化完成，可以发送请求，此时，
        * 消息将沿着客户端的出站链移动，直到送达真正的服务端
        * */
        ChannelFuture future = ctx.channel().writeAndFlush(request.retain());

        /*
        * 这行Listener代码也可以在ProxyToServerOutboundHandler@write中的promise添加
        * */
        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
                if (!future.isSuccess()) {
                    future.channel().close();
                }
                sended = true;
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        recved = true;
        try {
            for (ProxyResponseFilter filter : this.responseFilters){
                response = filter.filter(request,response);
            }
            if(response.status() != HttpResponseStatus.OK){
                response.headers().set(REMOTE_STATUS,response.status().code());
                response.setStatus(HttpResponseStatus.OK);
            }
            if(logger.isDebugEnabled()){
                logger.debug("server response code: {}",response.status().code());
                logger.debug("server response headers: {}",response.headers());
                logger.debug("server response body: {}",response.content().toString(Charset.defaultCharset()));
            }
            client2ProxyCtx.channel().writeAndFlush(response.retain()).addListener(ChannelFutureListener.CLOSE);
        } finally {
            request.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext proxy2ServerCtx, Throwable cause) throws Exception {
        if(sended && !recved && cause instanceof IOException){
            cause = new IgnoreException(MsgCode.E_THD_SVR_INSIDE_CLOSED);
        }
        try{
            proxy2ServerCtx.fireExceptionCaught(cause);
        } finally {
            request.release();
            client2ProxyCtx.close();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public synchronized ProxyToServerInboundHandler addFilter(ProxyResponseFilter filter){
        this.responseFilters.add(filter);
        return this;
    }
}
