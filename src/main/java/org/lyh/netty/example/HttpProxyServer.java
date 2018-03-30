package org.lyh.netty.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/1/31 10:39
 */
public class HttpProxyServer {
    public static int PORT = 7987;

    public static boolean isWindows;


    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup masterGroup;
        EventLoopGroup workerGroup;
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");
        // 线程数不宜过大，当并发数和线程数超多一定值，线程间频繁切换反而会使得性能降低
        if(isWindows){
            masterGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(100);
        } else {
            masterGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(100);
        }
        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(masterGroup,workerGroup)
                    .channel(isWindows ? NioServerSocketChannel.class : EpollServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,2048)
                    .handler(new LoggingHandler())
                    .childHandler(new ClientToProxyChannelInitializer());

            ChannelFuture future = bootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            masterGroup.shutdownGracefully();
        }
    }
}
