package org.lyh.http.proxy;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/1/31 10:39
 */
public class HttpProxyServer {

    private static final Logger logger = LoggerFactory.getLogger(HttpProxyServer.class);

    public static int PORT = 7987;

    public static boolean isWindows;

    public static void main(String[] args) throws InterruptedException {

        logger.info("entitys {}",EntitysManager.getInstance());
        isWindows = System.getProperty("os.name").toLowerCase().startsWith("win");

        try{
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(EventLoopGroupMannager.getMasterGroup(),EventLoopGroupMannager.getWorkerGroup())
                    .channel(isWindows ? NioServerSocketChannel.class : EpollServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,2048)
                    .handler(new LoggingHandler())
                    .childHandler(new ClientToProxyChannelInitializer());

            ChannelFuture future = bootstrap.bind(PORT).sync();
            future.channel().closeFuture().sync();
        } finally {
            EntitysManager.getInstance().stopWatchThread();
            EventLoopGroupMannager.shutdownGracefully();
        }
    }
}
