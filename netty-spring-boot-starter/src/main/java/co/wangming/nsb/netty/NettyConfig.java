package co.wangming.nsb.netty;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import lombok.Data;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created By WangMing On 2019-12-09
 **/
@Data
public class NettyConfig<T> {

    private static NettyConfigValue<Integer> port = null;
    private static Integer readerIdleTimeSeconds = null;
    private static Integer writerIdleTimeSeconds = null;
    private static Integer allIdleTimeSeconds = null;
    private static int bossGroupThreadSize = 1;
    private static int workGroupThreadSize = 1;

    private static NettyConfigValue<ByteBufAllocator> allocator = null;
    private static NettyConfigValue<RecvByteBufAllocator> rcvbufAllocator = null;
    private static NettyConfigValue<MessageSizeEstimator> messageSizeEstimator = null;

    private static NettyConfigValue<Integer> connectTimeoutMillis = null;
    private static NettyConfigValue<Integer> maxMessagesPerRead = null;
    private static NettyConfigValue<Integer> writeSpinCount = null;
    private static NettyConfigValue<Integer> writeBufferHighWaterMark = null;
    private static NettyConfigValue<Integer> writeBufferLowWaterMark = null;

    private static NettyConfigValue<Boolean> allowHalfClosure = null;
    private static NettyConfigValue<Boolean> autoRead = null;

    private static NettyConfigValue<Boolean> SO_BROADCAST = null;
    private static NettyConfigValue<Boolean> SO_KEEPALIVE = null;
    private static NettyConfigValue<Integer> SO_SNDBUF = null;
    private static NettyConfigValue<Integer> SO_RCVBUF = null;
    private static NettyConfigValue<Boolean> SO_REUSEADDR = null;
    private static NettyConfigValue<Integer> SO_LINGER = null;
    private static NettyConfigValue<Integer> SO_BACKLOG = null;
    private static NettyConfigValue<Integer> SO_TIMEOUT = null;

    private static NettyConfigValue<Boolean> TCP_NODELAY = null;

    private static NettyConfigValue<Integer> ipTos = null;
    private static NettyConfigValue<InetAddress> ipMulticastAddr = null;
    private static NettyConfigValue<NetworkInterface> ipMulticastIf = null;
    private static NettyConfigValue<Integer> ipMulticastTtl = null;
    private static NettyConfigValue<Boolean> ipMulticastLoopDisabled = null;

    private static NettyConfigValue<Boolean> singleEventexecutorPerGroup = null;

    public static NettyConfigValue<Integer> getPort() {
        return port;
    }

    public static void setPort(Integer port) {
        NettyConfigValue nettyConfigValue = new NettyConfigValue();
        nettyConfigValue.value = port;
        NettyConfig.port = nettyConfigValue;
    }

    public static Integer getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public static void setReaderIdleTimeSeconds(Integer readerIdleTimeSeconds) {
        NettyConfig.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public static Integer getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public static void setWriterIdleTimeSeconds(Integer writerIdleTimeSeconds) {
        NettyConfig.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public static Integer getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public static void setAllIdleTimeSeconds(Integer allIdleTimeSeconds) {
        NettyConfig.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    public static int getBossGroupThreadSize() {
        return bossGroupThreadSize;
    }

    public static void setBossGroupThreadSize(int bossGroupThreadSize) {
        NettyConfig.bossGroupThreadSize = bossGroupThreadSize;
    }

    public static int getWorkGroupThreadSize() {
        return workGroupThreadSize;
    }

    public static void setWorkGroupThreadSize(int workGroupThreadSize) {
        NettyConfig.workGroupThreadSize = workGroupThreadSize;
    }

    public static NettyConfigValue<ByteBufAllocator> getAllocator() {
        return allocator;
    }

    public static void setAllocator(String allocator) throws Exception {
        NettyConfig.allocator = getClassNettyConfigValue(allocator, ChannelOption.ALLOCATOR);
    }

    private static NettyConfigValue getClassNettyConfigValue(String className, ChannelOption channelOption) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (className == null) {
            return null;
        }

        Class value = Class.forName(className);

        NettyConfigValue nettyConfigValue = new NettyConfigValue();
        nettyConfigValue.value = value.newInstance();
        nettyConfigValue.name = channelOption;
        return nettyConfigValue;
    }

    private static NettyConfigValue getNettyConfigValue(Object ALLOCATOR, ChannelOption channelOption) {
        if (ALLOCATOR == null) {
            return null;
        }

        NettyConfigValue nettyConfigValue = new NettyConfigValue();
        nettyConfigValue.value = ALLOCATOR;
        nettyConfigValue.name = channelOption;
        return nettyConfigValue;
    }

    public static NettyConfigValue<RecvByteBufAllocator> getRcvbufAllocator() {
        return rcvbufAllocator;
    }

    public static void setRcvbufAllocator(String rcvbufAllocator) throws Exception {
        NettyConfig.rcvbufAllocator = getClassNettyConfigValue(rcvbufAllocator, ChannelOption.ALLOCATOR);
    }

    public static NettyConfigValue<MessageSizeEstimator> getMessageSizeEstimator() {
        return messageSizeEstimator;
    }

    public static void setMessageSizeEstimator(String messageSizeEstimator) throws Exception {
        NettyConfig.messageSizeEstimator = getClassNettyConfigValue(messageSizeEstimator, ChannelOption.ALLOCATOR);
    }

    public static NettyConfigValue<Integer> getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public static void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        NettyConfig.connectTimeoutMillis = getNettyConfigValue(connectTimeoutMillis, ChannelOption.CONNECT_TIMEOUT_MILLIS);
    }

    public static NettyConfigValue<Integer> getMaxMessagesPerRead() {
        return maxMessagesPerRead;
    }

    public static void setMaxMessagesPerRead(Integer maxMessagesPerRead) {
        NettyConfig.maxMessagesPerRead = getNettyConfigValue(maxMessagesPerRead, ChannelOption.MAX_MESSAGES_PER_READ);
    }

    public static NettyConfigValue<Integer> getWriteSpinCount() {
        return writeSpinCount;
    }

    public static void setWriteSpinCount(Integer writeSpinCount) {
        NettyConfig.writeSpinCount = getNettyConfigValue(writeSpinCount, ChannelOption.WRITE_SPIN_COUNT);
    }

    public static NettyConfigValue<Integer> getWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark;
    }

    public static void setWriteBufferHighWaterMark(Integer writeBufferHighWaterMark) {
        NettyConfig.writeBufferHighWaterMark = getNettyConfigValue(writeBufferHighWaterMark, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK);
    }

    public static NettyConfigValue<Integer> getWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark;
    }

    public static void setWriteBufferLowWaterMark(Integer writeBufferLowWaterMark) {
        NettyConfig.writeBufferLowWaterMark = getNettyConfigValue(writeBufferLowWaterMark, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK);
    }

    public static NettyConfigValue<Boolean> getAllowHalfClosure() {
        return allowHalfClosure;
    }

    public static void setAllowHalfClosure(Boolean allowHalfClosure) {
        NettyConfig.allowHalfClosure = getNettyConfigValue(allowHalfClosure, ChannelOption.ALLOW_HALF_CLOSURE);
    }

    public static NettyConfigValue<Boolean> getAutoRead() {
        return autoRead;
    }

    public static void setAutoRead(Boolean autoRead) {
        NettyConfig.autoRead = getNettyConfigValue(autoRead, ChannelOption.AUTO_READ);
    }

    public static NettyConfigValue<Boolean> getSoBroadcast() {
        return SO_BROADCAST;
    }

    public static void setSoBroadcast(Boolean soBroadcast) {
        SO_BROADCAST = getNettyConfigValue(soBroadcast, ChannelOption.SO_BROADCAST);
    }

    public static NettyConfigValue<Boolean> getSoKeepalive() {
        return SO_KEEPALIVE;
    }

    public static void setSoKeepalive(Boolean soKeepalive) {
        SO_KEEPALIVE = getNettyConfigValue(soKeepalive, ChannelOption.SO_KEEPALIVE);
    }

    public static NettyConfigValue<Integer> getSoSndbuf() {
        return SO_SNDBUF;
    }

    public static void setSoSndbuf(Integer soSndbuf) {
        SO_SNDBUF = getNettyConfigValue(soSndbuf, ChannelOption.SO_SNDBUF);
    }

    public static NettyConfigValue<Integer> getSoRcvbuf() {
        return SO_RCVBUF;
    }

    public static void setSoRcvbuf(Integer soRcvbuf) {
        SO_RCVBUF = getNettyConfigValue(soRcvbuf, ChannelOption.SO_RCVBUF);
    }

    public static NettyConfigValue<Boolean> getSoReuseaddr() {
        return SO_REUSEADDR;
    }

    public static void setSoReuseaddr(Boolean soReuseaddr) {
        SO_REUSEADDR = getNettyConfigValue(soReuseaddr, ChannelOption.SO_REUSEADDR);
    }

    public static NettyConfigValue<Integer> getSoLinger() {
        return SO_LINGER;
    }

    public static void setSoLinger(Integer soLinger) {
        SO_LINGER = getNettyConfigValue(soLinger, ChannelOption.SO_LINGER);
    }

    public static NettyConfigValue<Integer> getSoBacklog() {
        return SO_BACKLOG;
    }

    public static void setSoBacklog(Integer soBacklog) {
        SO_BACKLOG = getNettyConfigValue(soBacklog, ChannelOption.SO_BACKLOG);
    }

    public static NettyConfigValue<Integer> getSoTimeout() {
        return SO_TIMEOUT;
    }

    public static void setSoTimeout(Integer soTimeout) {
        SO_TIMEOUT = getNettyConfigValue(soTimeout, ChannelOption.SO_TIMEOUT);
    }

    public static NettyConfigValue<Integer> getIpTos() {
        return ipTos;
    }

    public static void setIpTos(Integer ipTos) {
        NettyConfig.ipTos = getNettyConfigValue(ipTos, ChannelOption.IP_TOS);
    }

    public static NettyConfigValue<InetAddress> getIpMulticastAddr() {
        return ipMulticastAddr;
    }

    public static void setIpMulticastAddr(InetAddress ipMulticastAddr) {
        NettyConfig.ipMulticastAddr = getNettyConfigValue(ipMulticastAddr, ChannelOption.IP_MULTICAST_ADDR);
    }

    public static NettyConfigValue<NetworkInterface> getIpMulticastIf() {
        return ipMulticastIf;
    }

    public static void setIpMulticastIf(NetworkInterface ipMulticastIf) {
        NettyConfig.ipMulticastIf = getNettyConfigValue(ipMulticastIf, ChannelOption.IP_MULTICAST_IF);
    }

    public static NettyConfigValue<Integer> getIpMulticastTtl() {
        return ipMulticastTtl;
    }

    public static void setIpMulticastTtl(Integer ipMulticastTtl) {
        NettyConfig.ipMulticastTtl = getNettyConfigValue(ipMulticastTtl, ChannelOption.IP_MULTICAST_TTL);
    }

    public static NettyConfigValue<Boolean> getIpMulticastLoopDisabled() {
        return ipMulticastLoopDisabled;
    }

    public static void setIpMulticastLoopDisabled(Boolean ipMulticastLoopDisabled) {
        NettyConfig.ipMulticastLoopDisabled = getNettyConfigValue(ipMulticastLoopDisabled, ChannelOption.IP_MULTICAST_LOOP_DISABLED);
    }

    public static NettyConfigValue<Boolean> getTcpNodelay() {
        return TCP_NODELAY;
    }

    public static void setTcpNodelay(Boolean tcpNodelay) {
        TCP_NODELAY = getNettyConfigValue(tcpNodelay, ChannelOption.TCP_NODELAY);
    }

    public static NettyConfigValue<Boolean> getSingleEventexecutorPerGroup() {
        return singleEventexecutorPerGroup;
    }

    public static void setSingleEventexecutorPerGroup(Boolean singleEventexecutorPerGroup) {
        NettyConfig.singleEventexecutorPerGroup = getNettyConfigValue(singleEventexecutorPerGroup, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
    }

    @Data
    public static class NettyConfigValue<T> {
        private ChannelOption name;
        private T value;
    }

}
