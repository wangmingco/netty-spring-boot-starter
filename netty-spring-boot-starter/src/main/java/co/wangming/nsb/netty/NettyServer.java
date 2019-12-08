package co.wangming.nsb.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
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

    private int port;

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
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture f = b.bind("localhost", port).sync();

            log.info("Netty Server listening at:{}", port);

            this.bossGroup = bossGroup;
            this.workerGroup = workerGroup;

//            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("", e);
            stop();
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
