package org.lyh.http.proxy.core;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.lyh.http.proxy.filter.AttackRequestFilter;


/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/3/27 17:50
 */
public class ClientToProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    public static final String FILE_CODEC = "file-codec";
    public static final String HTTP_DECODER = "http-decoder";
    public static final String HTTP_AGGREGATOR = "http-aggregator";
    public static final String CLIENT_TO_PROXY_HANDLER = "clientToProxyHandler";
    public static final String GLOBAL_EXCEPTION_HANDLER = "globalExceptionHandler";
    public static final String HTTP_ENCODER = "http-encoder";

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //ch.pipeline().addLast("http-codec",new HttpServerCodec());
        ch.pipeline().addLast(HTTP_DECODER,new HttpRequestDecoder());
        ch.pipeline().addLast(FILE_CODEC,new FileCodec());
        ch.pipeline().addLast(HTTP_AGGREGATOR,new HttpObjectAggregator(1024 * 1024 * 1024));
        ClientToProxyHandler clientToProxyHandler = new ClientToProxyHandler();
        clientToProxyHandler.addFilter(new AttackRequestFilter());
        ch.pipeline().addLast(CLIENT_TO_PROXY_HANDLER,clientToProxyHandler);
        ch.pipeline().addLast(GLOBAL_EXCEPTION_HANDLER,new GlobalExceptionHandler(ch));
        ch.pipeline().addLast(HTTP_ENCODER,new HttpResponseEncoder());
    }
}
