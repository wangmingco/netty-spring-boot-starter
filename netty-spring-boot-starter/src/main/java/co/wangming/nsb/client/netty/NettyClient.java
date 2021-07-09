package co.wangming.nsb.client.netty;

import co.wangming.nsb.server.netty.NettyServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created By WangMing On 2020-01-16
 **/
public enum NettyClient {

    INSTANCE;

    private EventLoopGroup group;
    private Bootstrap b;

    public void destroy() throws Exception {
        group.shutdownGracefully();
    }

    public void init() throws Exception {
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new NettyServerHandler());
                    }
                });

    }

    public Channel connect(String host, Integer port) {

        InetSocketAddress target = new InetSocketAddress(host, port);

        try {
            ChannelFuture c = b.connect(target);
            c.sync();
            Channel connectChannel = c.channel();
            return connectChannel;
        } catch (Exception e) {
            return null;
        }
    }
}
