package co.wangming.nsb.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-06
 **/
@Data
@Builder
@Slf4j
public class NettyServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void start() {

        log.info("Netty Server starting...");

        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new NettyServerHandler());
                        }
                    });

            setOption(b);

            int port = 8081;
            if (NettyConfig.getPORT() != null) {
                port = NettyConfig.getPORT().getValue();
            }

            b.bind("localhost", port).sync();

            log.info("Netty Server listening at:{}", port);

            this.bossGroup = bossGroup;
            this.workerGroup = workerGroup;

        } catch (InterruptedException e) {
            log.error("", e);
            stop();
        }
    }

    private void setOption(ServerBootstrap b) {
        setOption(b, NettyConfig.getALLOCATOR());
        setOption(b, NettyConfig.getRcvbufAllocator());
        setOption(b, NettyConfig.getMessageSizeEstimator());
        setOption(b, NettyConfig.getConnectTimeoutMillis());
        setOption(b, NettyConfig.getMaxMessagesPerRead());
        setOption(b, NettyConfig.getWriteSpinCount());
        setOption(b, NettyConfig.getWriteBufferHighWaterMark());
        setOption(b, NettyConfig.getWriteBufferLowWaterMark());
        setOption(b, NettyConfig.getAllowHalfClosure());
        setOption(b, NettyConfig.getAutoRead());
        setOption(b, NettyConfig.getSoBroadcast());
        setOption(b, NettyConfig.getSoKeepalive());
        setOption(b, NettyConfig.getSoSndbuf());
        setOption(b, NettyConfig.getSoRcvbuf());
        setOption(b, NettyConfig.getSoReuseaddr());
        setOption(b, NettyConfig.getSoBacklog());
        setOption(b, NettyConfig.getIpTos());
        setOption(b, NettyConfig.getIpMulticastAddr());
        setOption(b, NettyConfig.getIpMulticastIf());
        setOption(b, NettyConfig.getIpMulticastTtl());
        setOption(b, NettyConfig.getIpMulticastLoopDisabled());
        setOption(b, NettyConfig.getTcpNodelay());
        setOption(b, NettyConfig.getSingleEventexecutorPerGroup());
        setOption(b, NettyConfig.getSoLinger());
        setOption(b, NettyConfig.getSoTimeout());
    }

    private void setOption(ServerBootstrap b, NettyConfig.NettyConfigValue nettyConfig) {
        if (nettyConfig != null) {
            b.option(nettyConfig.getName(), nettyConfig.getValue());
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
