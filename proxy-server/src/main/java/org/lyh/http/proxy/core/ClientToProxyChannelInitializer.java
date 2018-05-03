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

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        //ch.pipeline().addLast("http-codec",new HttpServerCodec());
        ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
        ch.pipeline().addLast("file-chunk",new FileChunkAggregator());
        ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(1024 * 1024 * 1024));
        ClientToProxyHandler clientToProxyHandler = new ClientToProxyHandler();
        clientToProxyHandler.addFilter(new AttackRequestFilter());
        ch.pipeline().addLast("clientToProxyHandler",clientToProxyHandler);
        ch.pipeline().addLast("globalExceptionHandler",new GlobalExceptionHandler(ch));
        ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
    }
}
