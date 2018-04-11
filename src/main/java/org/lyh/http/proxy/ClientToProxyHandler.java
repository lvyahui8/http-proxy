package org.lyh.http.proxy;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/1/31 10:43
 */
public class ClientToProxyHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ClientToProxyHandler.class);
    private static final String HTTP_PROTOCOL = "http://";

    public static final String HTTP_SPL = "%0D%0A";

    private static EntitysManager entitysManager =  EntitysManager.getInstance();

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
        URI uri = new URI(msg.uri());
        HttpProxyEntity entity = entitysManager.getEntity(uri.getPath(),msg.method().name());
        if(entity != null && entity.getTargetUri() != null
                && entity.getTargetUri().trim().length() > 0){
            /* 过滤可能的头攻击 */
            checkHeaders(msg.headers());
            /* 找到可代理的对象 */
            URL url = new URL(entity.getTargetUri().startsWith("http://")
                    ? entity.getTargetUri() : HTTP_PROTOCOL + entity.getTargetUri());

            Bootstrap bootstrap = new Bootstrap();
            msg.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(HttpProxyServer.isWindows ? NioSocketChannel.class : EpollSocketChannel.class)
                    .handler(new ProxyToServerChannelInitializer(ctx,msg.copy()));

            bootstrap.connect(url.getHost(), url.getPort());
        } else {
            /* 直接响应错误信息 */
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.NOT_FOUND);
            response.headers().set(
                    HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.CLOSE
            );
            ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    /*
     * 过滤可能的头攻击
     */
    private void checkHeaders(HttpHeaders headers) {
        headers.forEach(header -> {
            if(header.getKey().contains(HTTP_SPL) || header.getValue().contains(HTTP_SPL)){
                throw new StandardException(MsgCode.E_HEAD_ATTACK);
            }
        });
    }

}
