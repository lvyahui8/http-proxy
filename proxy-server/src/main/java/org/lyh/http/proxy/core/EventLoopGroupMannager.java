package org.lyh.http.proxy.core;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.lyh.http.proxy.HttpProxyServer;

/**
 * @author lvyahui (lvyahui8@gmail.com,lvyahui8@126.com)
 * @since 2018/4/14 21:24
 */
final public class EventLoopGroupMannager {
    static EventLoopGroup masterGroup;
    static EventLoopGroup workerGroup;

    private EventLoopGroupMannager(){

    }

    static {
        // 线程数不宜过大，当并发数和线程数超多一定值，线程间频繁切换反而会使得性能降低
        // DefaultThreadFactory workerThreadFactory = new DefaultThreadFactory("proxy-worker", true);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        if(HttpProxyServer.isWindows){
            masterGroup = new NioEventLoopGroup(1);
            workerGroup = new NioEventLoopGroup(availableProcessors);
        } else {
            masterGroup = new EpollEventLoopGroup(1);
            workerGroup = new EpollEventLoopGroup(availableProcessors);
        }
    }

    public static EventLoopGroup getMasterGroup() {
        return masterGroup;
    }

    public static EventLoopGroup getWorkerGroup() {
        return workerGroup;
    }

    public static void shutdownGracefully(){
        workerGroup.shutdownGracefully();
        masterGroup.shutdownGracefully();
    }
}
