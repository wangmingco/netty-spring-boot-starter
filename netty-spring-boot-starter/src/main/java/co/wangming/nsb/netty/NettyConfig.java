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

    private static NettyConfigValue<Integer> PORT = null;

    private static NettyConfigValue<ByteBufAllocator> ALLOCATOR = null;
    private static NettyConfigValue<RecvByteBufAllocator> RCVBUF_ALLOCATOR = null;
    private static NettyConfigValue<MessageSizeEstimator> MESSAGE_SIZE_ESTIMATOR = null;

    private static NettyConfigValue<Integer> CONNECT_TIMEOUT_MILLIS = null;
    private static NettyConfigValue<Integer> MAX_MESSAGES_PER_READ = null;
    private static NettyConfigValue<Integer> WRITE_SPIN_COUNT = null;
    private static NettyConfigValue<Integer> WRITE_BUFFER_HIGH_WATER_MARK = null;
    private static NettyConfigValue<Integer> WRITE_BUFFER_LOW_WATER_MARK = null;

    private static NettyConfigValue<Boolean> ALLOW_HALF_CLOSURE = null;
    private static NettyConfigValue<Boolean> AUTO_READ = null;

    private static NettyConfigValue<Boolean> SO_BROADCAST = null;
    private static NettyConfigValue<Boolean> SO_KEEPALIVE = null;
    private static NettyConfigValue<Integer> SO_SNDBUF = null;
    private static NettyConfigValue<Integer> SO_RCVBUF = null;
    private static NettyConfigValue<Boolean> SO_REUSEADDR = null;
    private static NettyConfigValue<Integer> SO_LINGER = null;
    private static NettyConfigValue<Integer> SO_BACKLOG = null;
    private static NettyConfigValue<Integer> SO_TIMEOUT = null;

    private static NettyConfigValue<Integer> IP_TOS = null;
    private static NettyConfigValue<InetAddress> IP_MULTICAST_ADDR = null;
    private static NettyConfigValue<NetworkInterface> IP_MULTICAST_IF = null;
    private static NettyConfigValue<Integer> IP_MULTICAST_TTL = null;
    private static NettyConfigValue<Boolean> IP_MULTICAST_LOOP_DISABLED = null;

    private static NettyConfigValue<Boolean> TCP_NODELAY = null;

    private static NettyConfigValue<Boolean> SINGLE_EVENTEXECUTOR_PER_GROUP = null;

    public static NettyConfigValue<Integer> getPORT() {
        return PORT;
    }

    public static void setPORT(Integer PORT) {
        NettyConfigValue nettyConfigValue = new NettyConfigValue();
        nettyConfigValue.value = PORT;
        NettyConfig.PORT = nettyConfigValue;
    }

    public static NettyConfigValue<ByteBufAllocator> getALLOCATOR() {
        return ALLOCATOR;
    }

    public static void setALLOCATOR(String allocator) throws Exception {
        ALLOCATOR = getClassNettyConfigValue(allocator, ChannelOption.ALLOCATOR);
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
        return RCVBUF_ALLOCATOR;
    }

    public static void setRcvbufAllocator(String rcvbufAllocator) throws Exception {
        RCVBUF_ALLOCATOR = getClassNettyConfigValue(rcvbufAllocator, ChannelOption.ALLOCATOR);
    }

    public static NettyConfigValue<MessageSizeEstimator> getMessageSizeEstimator() {
        return MESSAGE_SIZE_ESTIMATOR;
    }

    public static void setMessageSizeEstimator(String messageSizeEstimator) throws Exception {
        MESSAGE_SIZE_ESTIMATOR = getClassNettyConfigValue(messageSizeEstimator, ChannelOption.ALLOCATOR);
    }

    public static NettyConfigValue<Integer> getConnectTimeoutMillis() {
        return CONNECT_TIMEOUT_MILLIS;
    }

    public static void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        CONNECT_TIMEOUT_MILLIS = getNettyConfigValue(connectTimeoutMillis, ChannelOption.CONNECT_TIMEOUT_MILLIS);
    }

    public static NettyConfigValue<Integer> getMaxMessagesPerRead() {
        return MAX_MESSAGES_PER_READ;
    }

    public static void setMaxMessagesPerRead(Integer maxMessagesPerRead) {
        MAX_MESSAGES_PER_READ = getNettyConfigValue(maxMessagesPerRead, ChannelOption.MAX_MESSAGES_PER_READ);
    }

    public static NettyConfigValue<Integer> getWriteSpinCount() {
        return WRITE_SPIN_COUNT;
    }

    public static void setWriteSpinCount(Integer writeSpinCount) {
        WRITE_SPIN_COUNT = getNettyConfigValue(writeSpinCount, ChannelOption.WRITE_SPIN_COUNT);
    }

    public static NettyConfigValue<Integer> getWriteBufferHighWaterMark() {
        return WRITE_BUFFER_HIGH_WATER_MARK;
    }

    public static void setWriteBufferHighWaterMark(Integer writeBufferHighWaterMark) {
        WRITE_BUFFER_HIGH_WATER_MARK = getNettyConfigValue(writeBufferHighWaterMark, ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK);
    }

    public static NettyConfigValue<Integer> getWriteBufferLowWaterMark() {
        return WRITE_BUFFER_LOW_WATER_MARK;
    }

    public static void setWriteBufferLowWaterMark(Integer writeBufferLowWaterMark) {
        WRITE_BUFFER_LOW_WATER_MARK = getNettyConfigValue(writeBufferLowWaterMark, ChannelOption.WRITE_BUFFER_LOW_WATER_MARK);
    }

    public static NettyConfigValue<Boolean> getAllowHalfClosure() {
        return ALLOW_HALF_CLOSURE;
    }

    public static void setAllowHalfClosure(Boolean allowHalfClosure) {
        ALLOW_HALF_CLOSURE = getNettyConfigValue(allowHalfClosure, ChannelOption.ALLOW_HALF_CLOSURE);
    }

    public static NettyConfigValue<Boolean> getAutoRead() {
        return AUTO_READ;
    }

    public static void setAutoRead(Boolean autoRead) {
        AUTO_READ = getNettyConfigValue(autoRead, ChannelOption.AUTO_READ);
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
        return IP_TOS;
    }

    public static void setIpTos(Integer ipTos) {
        IP_TOS = getNettyConfigValue(ipTos, ChannelOption.IP_TOS);
    }

    public static NettyConfigValue<InetAddress> getIpMulticastAddr() {
        return IP_MULTICAST_ADDR;
    }

    public static void setIpMulticastAddr(InetAddress ipMulticastAddr) {
        IP_MULTICAST_ADDR = getNettyConfigValue(ipMulticastAddr, ChannelOption.IP_MULTICAST_ADDR);
    }

    public static NettyConfigValue<NetworkInterface> getIpMulticastIf() {
        return IP_MULTICAST_IF;
    }

    public static void setIpMulticastIf(NetworkInterface ipMulticastIf) {
        IP_MULTICAST_IF = getNettyConfigValue(ipMulticastIf, ChannelOption.IP_MULTICAST_IF);
    }

    public static NettyConfigValue<Integer> getIpMulticastTtl() {
        return IP_MULTICAST_TTL;
    }

    public static void setIpMulticastTtl(Integer ipMulticastTtl) {
        IP_MULTICAST_TTL = getNettyConfigValue(ipMulticastTtl, ChannelOption.IP_MULTICAST_TTL);
    }

    public static NettyConfigValue<Boolean> getIpMulticastLoopDisabled() {
        return IP_MULTICAST_LOOP_DISABLED;
    }

    public static void setIpMulticastLoopDisabled(Boolean ipMulticastLoopDisabled) {
        IP_MULTICAST_LOOP_DISABLED = getNettyConfigValue(ipMulticastLoopDisabled, ChannelOption.IP_MULTICAST_LOOP_DISABLED);
    }

    public static NettyConfigValue<Boolean> getTcpNodelay() {
        return TCP_NODELAY;
    }

    public static void setTcpNodelay(Boolean tcpNodelay) {
        TCP_NODELAY = getNettyConfigValue(tcpNodelay, ChannelOption.TCP_NODELAY);
    }

    public static NettyConfigValue<Boolean> getSingleEventexecutorPerGroup() {
        return SINGLE_EVENTEXECUTOR_PER_GROUP;
    }

    public static void setSingleEventexecutorPerGroup(Boolean singleEventexecutorPerGroup) {
        SINGLE_EVENTEXECUTOR_PER_GROUP = getNettyConfigValue(singleEventexecutorPerGroup, ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
    }

    @Data
    public static class NettyConfigValue<T> {
        private ChannelOption name;
        private T value;
    }

}
