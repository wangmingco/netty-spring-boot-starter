package co.wangming.nsb.springboot;

import io.netty.channel.ChannelOption;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created By WangMing On 2019-12-06
 **/
@ConfigurationProperties(prefix = "spring.boot.netty")
public class SpringBootNettyProperties {

    /********************************************************************
     ***********************Netty 服务配置参数*****************************
     ********************************************************************/
    // Netty启动端口
    private String address;
    private Integer port = 7001;
    private Integer readerIdleTimeSeconds = 5;
    private Integer writerIdleTimeSeconds = 5;
    private Integer allIdleTimeSeconds = 5;
    private Integer bossGroupThreadSize = 1;
    private Integer workGroupThreadSize = 1;
//    private ChannelInboundHandler nettyServerHandler;

    /********************************************************************
     **************Netty #{@link ChannelOption} 配置参数******************
     ********************************************************************/
    /**
     * #{@link ChannelOption#ALLOCATOR}
     */
//    private ByteBufAllocator allocator = null;

    /**
     * #{@link ChannelOption#RCVBUF_ALLOCATOR}
     */
//    private RecvByteBufAllocator rcvbufAllocator = null;

    /**
     * #{@link ChannelOption#MESSAGE_SIZE_ESTIMATOR}
     */
//    private MessageSizeEstimator messageSizeEstimator = null;

    /**
     * #{@link ChannelOption#CONNECT_TIMEOUT_MILLIS}
     */
    private Integer connectTimeoutMillis = null;

    /**
     * #{@link ChannelOption#MAX_MESSAGES_PER_READ}
     */
    private Integer maxMessagesPerRead = null;

    /**
     * #{@link ChannelOption#WRITE_SPIN_COUNT}
     */
    private Integer writeSpinCount = null;

    /**
     * #{@link ChannelOption#WRITE_BUFFER_HIGH_WATER_MARK}
     */
    private Integer writeBufferHighWaterMark = null;

    /**
     * #{@link ChannelOption#WRITE_BUFFER_LOW_WATER_MARK}
     */
    private Integer writeBufferLowWaterMark = null;

    /**
     * #{@link ChannelOption#ALLOW_HALF_CLOSURE}
     */
    private Boolean allowHalfClosure = null;

    /**
     * #{@link ChannelOption#AUTO_READ}
     */
    private Boolean autoRead = null;

    /**
     * #{@link ChannelOption#SO_BROADCAST}
     */
    private Boolean soBroadcast = null;

    /**
     * #{@link ChannelOption#SO_KEEPALIVE}
     */
    private Boolean soKeepalive = null;

    /**
     * #{@link ChannelOption#SO_SNDBUF}
     */
    private Integer soSndbuf = null;

    /**
     * #{@link ChannelOption#SO_RCVBUF}
     */
    private Integer soRcvbuf = null;

    /**
     * #{@link ChannelOption#SO_REUSEADDR}
     */
    private Boolean soReuseaddr = null;

    /**
     * #{@link ChannelOption#SO_LINGER}
     */
    private Integer soLinger = null;

    /**
     * #{@link ChannelOption#SO_BACKLOG}
     */
    private Integer soBacklog = null;

    /**
     * #{@link ChannelOption#SO_TIMEOUT}
     */
    private Integer soTimeout = null;

    /**
     * #{@link ChannelOption#IP_TOS}
     */
    private Integer ipTos = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_ADDR}
     */
    private InetAddress ipMulticastAddr = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_IF}
     */
    private NetworkInterface ipMulticastIf = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_TTL}
     */
    private Integer ipMulticastTtl = null;

    /**
     * #{@link ChannelOption#IP_MULTICAST_LOOP_DISABLED}
     */
    private Boolean ipMulticastLoopDisabled = null;

    /**
     * #{@link ChannelOption#TCP_NODELAY}
     */
    private Boolean tcpNodelay = null;

    /**
     * #{@link ChannelOption#SINGLE_EVENTEXECUTOR_PER_GROUP}
     */
    private Boolean singleEventexecutorPerGroup = null;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(Integer readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public Integer getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(Integer writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public Integer getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(Integer allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    public Integer getBossGroupThreadSize() {
        return bossGroupThreadSize;
    }

    public void setBossGroupThreadSize(Integer bossGroupThreadSize) {
        this.bossGroupThreadSize = bossGroupThreadSize;
    }

    public Integer getWorkGroupThreadSize() {
        return workGroupThreadSize;
    }

    public void setWorkGroupThreadSize(Integer workGroupThreadSize) {
        this.workGroupThreadSize = workGroupThreadSize;
    }

    public Integer getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    public Integer getMaxMessagesPerRead() {
        return maxMessagesPerRead;
    }

    public void setMaxMessagesPerRead(Integer maxMessagesPerRead) {
        this.maxMessagesPerRead = maxMessagesPerRead;
    }

    public Integer getWriteSpinCount() {
        return writeSpinCount;
    }

    public void setWriteSpinCount(Integer writeSpinCount) {
        this.writeSpinCount = writeSpinCount;
    }

    public Integer getWriteBufferHighWaterMark() {
        return writeBufferHighWaterMark;
    }

    public void setWriteBufferHighWaterMark(Integer writeBufferHighWaterMark) {
        this.writeBufferHighWaterMark = writeBufferHighWaterMark;
    }

    public Integer getWriteBufferLowWaterMark() {
        return writeBufferLowWaterMark;
    }

    public void setWriteBufferLowWaterMark(Integer writeBufferLowWaterMark) {
        this.writeBufferLowWaterMark = writeBufferLowWaterMark;
    }

    public Boolean getAllowHalfClosure() {
        return allowHalfClosure;
    }

    public void setAllowHalfClosure(Boolean allowHalfClosure) {
        this.allowHalfClosure = allowHalfClosure;
    }

    public Boolean getAutoRead() {
        return autoRead;
    }

    public void setAutoRead(Boolean autoRead) {
        this.autoRead = autoRead;
    }

    public Boolean getSoBroadcast() {
        return soBroadcast;
    }

    public void setSoBroadcast(Boolean soBroadcast) {
        this.soBroadcast = soBroadcast;
    }

    public Boolean getSoKeepalive() {
        return soKeepalive;
    }

    public void setSoKeepalive(Boolean soKeepalive) {
        this.soKeepalive = soKeepalive;
    }

    public Integer getSoSndbuf() {
        return soSndbuf;
    }

    public void setSoSndbuf(Integer soSndbuf) {
        this.soSndbuf = soSndbuf;
    }

    public Integer getSoRcvbuf() {
        return soRcvbuf;
    }

    public void setSoRcvbuf(Integer soRcvbuf) {
        this.soRcvbuf = soRcvbuf;
    }

    public Boolean getSoReuseaddr() {
        return soReuseaddr;
    }

    public void setSoReuseaddr(Boolean soReuseaddr) {
        this.soReuseaddr = soReuseaddr;
    }

    public Integer getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(Integer soLinger) {
        this.soLinger = soLinger;
    }

    public Integer getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(Integer soBacklog) {
        this.soBacklog = soBacklog;
    }

    public Integer getSoTimeout() {
        return soTimeout;
    }

    public void setSoTimeout(Integer soTimeout) {
        this.soTimeout = soTimeout;
    }

    public Integer getIpTos() {
        return ipTos;
    }

    public void setIpTos(Integer ipTos) {
        this.ipTos = ipTos;
    }

    public InetAddress getIpMulticastAddr() {
        return ipMulticastAddr;
    }

    public void setIpMulticastAddr(InetAddress ipMulticastAddr) {
        this.ipMulticastAddr = ipMulticastAddr;
    }

    public NetworkInterface getIpMulticastIf() {
        return ipMulticastIf;
    }

    public void setIpMulticastIf(NetworkInterface ipMulticastIf) {
        this.ipMulticastIf = ipMulticastIf;
    }

    public Integer getIpMulticastTtl() {
        return ipMulticastTtl;
    }

    public void setIpMulticastTtl(Integer ipMulticastTtl) {
        this.ipMulticastTtl = ipMulticastTtl;
    }

    public Boolean getIpMulticastLoopDisabled() {
        return ipMulticastLoopDisabled;
    }

    public void setIpMulticastLoopDisabled(Boolean ipMulticastLoopDisabled) {
        this.ipMulticastLoopDisabled = ipMulticastLoopDisabled;
    }

    public Boolean getTcpNodelay() {
        return tcpNodelay;
    }

    public void setTcpNodelay(Boolean tcpNodelay) {
        this.tcpNodelay = tcpNodelay;
    }

    public Boolean getSingleEventexecutorPerGroup() {
        return singleEventexecutorPerGroup;
    }

    public void setSingleEventexecutorPerGroup(Boolean singleEventexecutorPerGroup) {
        this.singleEventexecutorPerGroup = singleEventexecutorPerGroup;
    }
}
