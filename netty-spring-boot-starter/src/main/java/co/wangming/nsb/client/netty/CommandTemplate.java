package co.wangming.nsb.client.netty;

import co.wangming.nsb.common.SpringContext;
import co.wangming.nsb.server.processors.ProtocolProcessor;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created By WangMing On 2020-01-02
 **/
public class CommandTemplate<T> {

    private static final Logger log = LoggerFactory.getLogger(CommandTemplate.class);

    private Class tClass;

    private Bootstrap b;
    private Channel connectChannel;

    private String host;
    private Integer port;

    public CommandTemplate() {
        this(null);
    }

    public CommandTemplate(Bootstrap b) {
        this.b = b;
    }

    public boolean isConnected() {
        return connectChannel != null && connectChannel.isActive() && connectChannel.isOpen();
    }

    public void disconnect() throws InterruptedException {
        connectChannel.disconnect().sync();
    }

    public ChannelFuture connect() {
        if (connectChannel != null && isConnected()) {
            try {
                disconnect();
            } catch (InterruptedException e) {

            }
        }
        ChannelFuture c = b.connect(new InetSocketAddress(host, port));
        try {
            c.sync();
        } catch (InterruptedException e) {
        }
        connectChannel = c.channel();
        return c;
    }

    public void syncWrite(Integer messageId, T msg) {
        if (!isConnected()) {
            connect();
        }

        if (tClass == null) {
            return;
        }
        // TODO 抽象出能够自动识别多种协议
        String protocolProcessorName = tClass.getSimpleName() + "ProtocolProcessor";
        ProtocolProcessor<Void, T> protocolProcessor = (ProtocolProcessor) SpringContext.getBean(protocolProcessorName);
        byte[] bytearray = null;
        try {
            bytearray = protocolProcessor.serialize(null, msg);
        } catch (Exception e) {
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

    public Class gettClass() {
        return tClass;
    }

    public void settClass(Class tClass) {
        this.tClass = tClass;
    }

    public Bootstrap getB() {
        return b;
    }

    public void setB(Bootstrap b) {
        this.b = b;
    }

    public Channel getConnectChannel() {
        return connectChannel;
    }

    public void setConnectChannel(Channel connectChannel) {
        this.connectChannel = connectChannel;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
