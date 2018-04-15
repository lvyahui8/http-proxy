package org.lyh.http.proxy;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/19 16:18
 */
public class ProxyToServerHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ProxyToServerHandler.class);

    private final ChannelHandlerContext client2ProxyCtx;

    private FullHttpRequest request;

    private List<ProxyResponseFilter> responseFilters ;

    public ProxyToServerHandler(ChannelHandlerContext client2ProxyCtx, FullHttpRequest request) {
        this.client2ProxyCtx = client2ProxyCtx;
        this.request = request;
        this.responseFilters = new ArrayList<>();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture future = ctx.channel().writeAndFlush(request.retain());
        future.addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) throws Exception {
        try {
            for (ProxyResponseFilter filter : this.responseFilters){
                response = filter.filter(request,response);
            }
            client2ProxyCtx.channel().writeAndFlush(response.retain()).addListener(ChannelFutureListener.CLOSE);
        } finally {
            request.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext proxy2ServerCtx, Throwable cause) throws Exception {
        try{
            client2ProxyCtx.close();
            request.release();
        } finally {
            proxy2ServerCtx.pipeline().fireExceptionCaught(cause);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public synchronized ProxyToServerHandler addFilter(ProxyResponseFilter filter){
        this.responseFilters.add(filter);
        return this;
    }
}
