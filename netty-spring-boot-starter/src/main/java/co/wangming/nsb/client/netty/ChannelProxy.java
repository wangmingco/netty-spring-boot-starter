package co.wangming.nsb.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ChannelProxy<T> {

    private static final Logger log = LoggerFactory.getLogger(ChannelProxy.class);

    private Channel connectChannel;

    private String host;
    private Integer port;

    public ChannelProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        this.connectChannel = NettyClient.INSTANCE.connect(host, port);
        if (this.connectChannel == null) {
            log.error("连接失败:[{}:{}]", host, port);
        }
    }

    public boolean isConnected() {
        return connectChannel != null && connectChannel.isActive() && connectChannel.isOpen();
    }

    public void disconnect() throws InterruptedException {
        connectChannel.disconnect().sync();
    }

    public void syncWrite(Integer messageId, byte[] bytearray) {

        if (!isConnected()) {
            for (int i = 0; i < 5; i++) {
                connect();
                if (!isConnected()) {
                    try {
                        TimeUnit.SECONDS.sleep(i + 1);
                    } catch (InterruptedException e) {
                        log.error("", e);
                    }
                }
            }
        }

        ByteBuf request = ByteBufAllocator.DEFAULT.heapBuffer(bytearray.length)
                .writeByte(messageId)
                .writeByte(bytearray.length)
                .writeBytes(bytearray);

        ChannelFuture result = connectChannel.writeAndFlush(request);
        result.addListener(future -> {
            if (future.isSuccess()) {
                log.debug("数据发送成功");
            } else {
                log.error("数据发送失败", future.cause());
            }
        });
    }

}
