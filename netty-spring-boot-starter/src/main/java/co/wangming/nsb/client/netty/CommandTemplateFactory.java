package co.wangming.nsb.client.netty;

import co.wangming.nsb.server.netty.NettyReciveHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created By WangMing On 2020-01-16
 **/
public enum CommandTemplateFactory {

    INSTANCE;

    private EventLoopGroup group;
    private Bootstrap b;

    public CommandTemplate instance() {
        return new CommandTemplate(b);
    }

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
                        p.addLast(new NettyReciveHandler());
                    }
                });

    }
}
