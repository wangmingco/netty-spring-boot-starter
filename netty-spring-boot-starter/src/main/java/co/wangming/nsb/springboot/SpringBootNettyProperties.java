package co.wangming.nsb.springboot;

import io.netty.channel.ChannelOption;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;
import java.net.NetworkInterface;

/**
 * Created By WangMing On 2019-12-06
 **/
@ConfigurationProperties(prefix = "spring.boot.netty")
@Data
public class SpringBootNettyProperties {

    /********************************************************************
     ***********************Netty 服务配置参数*****************************
     ********************************************************************/
    // Netty启动端口
    private String address = "localhost";
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


}
