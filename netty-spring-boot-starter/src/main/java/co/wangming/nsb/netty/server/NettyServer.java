package co.wangming.nsb.netty.server;

import co.wangming.nsb.netty.NettyReciveHandler;
import co.wangming.nsb.springboot.SpringBootNettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created By WangMing On 2019-12-06
 **/
@Data
@Builder
@Slf4j
public class NettyServer {

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public void start(SpringBootNettyProperties springBootNettyProperties) {

        log.info("Netty Server starting...");

        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup(springBootNettyProperties.getBossGroupThreadSize());
            EventLoopGroup workerGroup = new NioEventLoopGroup(springBootNettyProperties.getWorkGroupThreadSize());
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new NettyReciveHandler())
                                    .addLast(new IdleStateHandler(
                                            springBootNettyProperties.getReaderIdleTimeSeconds(),
                                            springBootNettyProperties.getWriterIdleTimeSeconds(),
                                            springBootNettyProperties.getAllIdleTimeSeconds()))
                            ;
                        }
                    });

            setOption(b, springBootNettyProperties);

            int port = springBootNettyProperties.getPort();

            ChannelFuture bindChannelFuture = null;
            if (springBootNettyProperties.getAddress() != null) {
                bindChannelFuture = b.bind(springBootNettyProperties.getAddress(), port).sync();
            } else {
                bindChannelFuture = b.bind(getServerIp(), port).sync();
            }

            bindChannelFuture.sync();
            log.info("Netty Server listening at:{}", bindChannelFuture.channel().localAddress());

            this.bossGroup = bossGroup;
            this.workerGroup = workerGroup;

        } catch (InterruptedException e) {
            log.error("", e);
            stop();
        }
    }

    public static String getServerIp() {
        String localip = null;// 本地IP，如果没有配置外网IP则返回它
        String netip = null;// 外网IP
        try {
            Enumeration netInterfaces = NetworkInterface.getNetworkInterfaces();
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = (NetworkInterface) netInterfaces.nextElement();
                Enumeration address = ni.getInetAddresses();
                while (address.hasMoreElements()) {

                    InetAddress ip = (InetAddress) address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        netip = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 内网IP
                        localip = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            return "127.0.0.1";
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }

    private void setOption(ServerBootstrap b, SpringBootNettyProperties springBootNettyProperties) {
//        setOption(b, ChannelOption.ALLOCATOR, springBootNettyProperties.getAllocator());
//        setOption(b, ChannelOption.RCVBUF_ALLOCATOR, springBootNettyProperties.getRcvbufAllocator());
//        setOption(b, ChannelOption.MESSAGE_SIZE_ESTIMATOR, springBootNettyProperties.getMessageSizeEstimator());
        setOption(b, ChannelOption.CONNECT_TIMEOUT_MILLIS, springBootNettyProperties.getConnectTimeoutMillis());
        setOption(b, ChannelOption.MAX_MESSAGES_PER_READ, springBootNettyProperties.getMaxMessagesPerRead());
        setOption(b, ChannelOption.WRITE_SPIN_COUNT, springBootNettyProperties.getWriteSpinCount());
        setOption(b, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, springBootNettyProperties.getWriteBufferHighWaterMark());
        setOption(b, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, springBootNettyProperties.getWriteBufferLowWaterMark());
        setOption(b, ChannelOption.ALLOW_HALF_CLOSURE, springBootNettyProperties.getAllowHalfClosure());
        setOption(b, ChannelOption.AUTO_READ, springBootNettyProperties.getAutoRead());
        setOption(b, ChannelOption.SO_BROADCAST, springBootNettyProperties.getSoBroadcast());
        setOption(b, ChannelOption.SO_KEEPALIVE, springBootNettyProperties.getSoKeepalive());
        setOption(b, ChannelOption.SO_SNDBUF, springBootNettyProperties.getSoSndbuf());
        setOption(b, ChannelOption.SO_RCVBUF, springBootNettyProperties.getSoRcvbuf());
        setOption(b, ChannelOption.SO_REUSEADDR, springBootNettyProperties.getSoReuseaddr());
        setOption(b, ChannelOption.SO_BACKLOG, springBootNettyProperties.getSoBacklog());
        setOption(b, ChannelOption.IP_TOS, springBootNettyProperties.getIpTos());
        setOption(b, ChannelOption.IP_MULTICAST_ADDR, springBootNettyProperties.getIpMulticastAddr());
        setOption(b, ChannelOption.IP_MULTICAST_IF, springBootNettyProperties.getIpMulticastIf());
        setOption(b, ChannelOption.IP_MULTICAST_TTL, springBootNettyProperties.getIpMulticastTtl());
        setOption(b, ChannelOption.IP_MULTICAST_LOOP_DISABLED, springBootNettyProperties.getIpMulticastLoopDisabled());
        setOption(b, ChannelOption.TCP_NODELAY, springBootNettyProperties.getTcpNodelay());
        setOption(b, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP, springBootNettyProperties.getSingleEventexecutorPerGroup());
        setOption(b, ChannelOption.SO_LINGER, springBootNettyProperties.getSoLinger());
        setOption(b, ChannelOption.SO_TIMEOUT, springBootNettyProperties.getSoTimeout());
    }

    private void setOption(ServerBootstrap b, ChannelOption option, Object value) {
        if (value != null) {
            b.option(option, value);
        }
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
