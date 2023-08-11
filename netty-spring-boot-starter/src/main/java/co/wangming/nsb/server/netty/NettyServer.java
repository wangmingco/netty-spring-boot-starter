package co.wangming.nsb.server.netty;

import co.wangming.nsb.common.springboot.SpringBootNettyProperties;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created By WangMing On 2019-12-06
 **/
public class NettyServer {

    private static final Logger log = LoggerFactory.getLogger(NettyServer.class);

    private EventLoopGroup tcpBossGroup;
    private EventLoopGroup tcpWorkerGroup;
    private EventLoopGroup udpBossGroup;
    private EventLoopGroup udpWorkerGroup;

    public void startTCP(SpringBootNettyProperties springBootNettyProperties) {
        int port = springBootNettyProperties.getPort();
        if (port == 0) {
            log.info("Netty TCP Server Stopped");
            return;
        }

        log.info("Netty TCP Server Starting...");

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
                                    .addLast(new NettyServerTCPHandler())
                                    .addLast(new IdleStateHandler(
                                            springBootNettyProperties.getReaderIdleTimeSeconds(),
                                            springBootNettyProperties.getWriterIdleTimeSeconds(),
                                            springBootNettyProperties.getAllIdleTimeSeconds()))
                            ;
                        }
                    });

            setOption(b, springBootNettyProperties);

            String ip = "0.0.0.0";
            if (springBootNettyProperties.getAddress() != null) {
                ip = springBootNettyProperties.getAddress();
            } else {
                ip = getServerIp();
            }

            ChannelFuture bindChannelFuture = null;
            bindChannelFuture = b.bind(ip, port).sync();
            bindChannelFuture.sync();
            log.info("Netty TCP Server Listening At [{}:{}]", ip, port);

            this.tcpBossGroup = bossGroup;
            this.tcpWorkerGroup = workerGroup;

        } catch (InterruptedException e) {
            log.error("", e);
            stop();
        }
    }

    public void startUDP(SpringBootNettyProperties springBootNettyProperties) {
        int port = springBootNettyProperties.getPort();
        if (port == 0) {
            log.info("Netty UDP Server Stopped");
            return;
        }

        log.info("Netty UDP Server Starting...");

        try {
            EventLoopGroup bossGroup = new NioEventLoopGroup(springBootNettyProperties.getBossGroupThreadSize());

            Bootstrap b = new Bootstrap();
            b.group(bossGroup)
                    .channel(NioDatagramChannel.class)
//                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new NettyServerUDPHandler());

            setOption(b, springBootNettyProperties);

            String ip = "0.0.0.0";
            if (springBootNettyProperties.getAddress() != null) {
                ip = springBootNettyProperties.getAddress();
            } else {
                ip = getServerIp();
            }

            ChannelFuture bindChannelFuture = null;
            bindChannelFuture = b.bind(ip, port).sync();
            bindChannelFuture.sync();
            log.info("Netty UDP Server Listening At [{}:{}]", ip, port);

            this.udpBossGroup = bossGroup;

        } catch (InterruptedException e) {
            log.error("", e);
            stop();
        }
    }

    public static String getServerIp() {
        if (1 == 1) {
            return "0.0.0.0";
        }

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
            return "0.0.0.0";
        }

        if (netip != null && !"".equals(netip)) {
            return netip;
        } else {
            return localip;
        }
    }

    private void setOption(AbstractBootstrap b, SpringBootNettyProperties springBootNettyProperties) {
        setOption(b, ChannelOption.ALLOCATOR, springBootNettyProperties.getAllocator());
        setOption(b, ChannelOption.RCVBUF_ALLOCATOR, springBootNettyProperties.getRcvbufAllocator());
        setOption(b, ChannelOption.MESSAGE_SIZE_ESTIMATOR, springBootNettyProperties.getMessageSizeEstimator());
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

    private void setOption(AbstractBootstrap b, ChannelOption option, Object value) {
        if (value != null) {
            b.option(option, value);
        }
    }

    public void stop() {
        tcpBossGroup.shutdownGracefully();
        tcpWorkerGroup.shutdownGracefully();
        udpBossGroup.shutdownGracefully();
    }
}
