package co.wangming.nsb.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class ChannelProxy<T> {

    private static final Logger log = LoggerFactory.getLogger(ChannelProxy.class);

    private Channel connectChannel;

    private final String protocol;
    private final String host;
    private final Integer port;

    public ChannelProxy(String protocol, String host, Integer port) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
    }

    public void connect() throws InterruptedException {
        this.connectChannel = NettyClient.INSTANCE.connect(host, port);
        if (this.connectChannel == null) {
            log.error("连接失败:[{}:{}]", host, port);
        } else {
            log.info("连接成功:[{}:{}]", host, port);
        }
    }

    public boolean isConnected() {
        return connectChannel != null && connectChannel.isActive() && connectChannel.isOpen();
    }

    public void disconnect() throws InterruptedException {
        connectChannel.disconnect().sync();
    }

    public void syncWrite(Integer messageId, byte[] bytearray) {

        ByteBuf request = ByteBufAllocator.DEFAULT.heapBuffer(bytearray.length)
                .writeByte(messageId)
                .writeByte(bytearray.length)
                .writeBytes(bytearray);

        if (protocol.toLowerCase().equals("tcp")) {
            writeTcp(request);
        } else {
            writeUdp(request);
        }
    }

    private void writeTcp(ByteBuf request) {
        if (!isConnected()) {
            for (int i = 0; i < 5; i++) {
                try {
                    connect();
                    if (!isConnected()) {
                        TimeUnit.SECONDS.sleep(i + 1);
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
        }

        ChannelFuture result = connectChannel.writeAndFlush(request);
        result.addListener(future -> {
            if (future.isSuccess()) {
                log.debug("数据发送成功");
            } else {
                log.error("数据发送失败", future.cause());
            }
        });
    }

    private void writeUdp(ByteBuf request) {
        InetSocketAddress senderAddress = InetSocketAddress.createUnresolved(host, port);

        DatagramPacket responsePacket = new DatagramPacket(request, senderAddress);
        this.connectChannel.writeAndFlush(responsePacket).addListener(listener -> {
            if (listener.isSuccess()) {
                log.debug("消息发送成功");
            } else {
                log.error("消息发送失败", listener.cause());
            }
        });
    }
}
