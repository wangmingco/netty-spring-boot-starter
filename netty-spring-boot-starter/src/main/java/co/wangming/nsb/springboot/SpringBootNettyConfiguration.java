package co.wangming.nsb.springboot;

import co.wangming.nsb.netty.NettyConfig;
import co.wangming.nsb.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created By WangMing On 2019-12-06
 **/
@Configuration
@EnableConfigurationProperties(SpringBootNettyProperties.class)
@Slf4j
public class SpringBootNettyConfiguration {

    @Autowired
    private SpringBootNettyProperties springBootNettyProperties;

    /**
     * springboot 启动时自动加载NettyStarter, 例如CommandLineRunner 启动netty服务器.
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(NettyStarter.class)
    public NettyStarter nettyStarter() {
        return new NettyStarter();
    }

    @Bean
    @ConditionalOnMissingBean(SpringContext.class)
    public SpringContext springContext() {
        return new SpringContext();
    }

    @Component
    public static class NettyStarter implements InitializingBean, DisposableBean {

        @Resource
        private SpringBootNettyProperties springBootNettyProperties;

        private NettyServer nettyServer;

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("Starting The Netty Server");
            setOptions();

            NettyServer nettyServer = NettyServer.builder().build();

            nettyServer.start();

            this.nettyServer = nettyServer;
            log.info("Started The Netty Server");
        }

        @Override
        public void destroy() throws Exception {
            log.info("Stopping The Netty Server");

            nettyServer.stop();

            log.info("Stopped The Netty Server");
        }


        private void setOptions() throws Exception {
            NettyConfig.setPort(springBootNettyProperties.getPORT());
            NettyConfig.setAllIdleTimeSeconds(springBootNettyProperties.getAllIdleTimeSeconds());
            NettyConfig.setReaderIdleTimeSeconds(springBootNettyProperties.getReaderIdleTimeSeconds());
            NettyConfig.setWriterIdleTimeSeconds(springBootNettyProperties.getWriterIdleTimeSeconds());
            NettyConfig.setBossGroupThreadSize(springBootNettyProperties.getBossGroupThreadSize());
            NettyConfig.setWorkGroupThreadSize(springBootNettyProperties.getWorkGroupThreadSize());

            NettyConfig.setAllocator(springBootNettyProperties.getAllocator());
            NettyConfig.setRcvbufAllocator(springBootNettyProperties.getRcvbufAllocator());
            NettyConfig.setMessageSizeEstimator(springBootNettyProperties.getMessageSizeEstimator());
            NettyConfig.setConnectTimeoutMillis(springBootNettyProperties.getConnectTimeoutMillis());
            NettyConfig.setMaxMessagesPerRead(springBootNettyProperties.getMaxMessagesPerRead());
            NettyConfig.setWriteSpinCount(springBootNettyProperties.getWriteSpinCount());
            NettyConfig.setWriteBufferHighWaterMark(springBootNettyProperties.getWriteBufferHighWaterMark());
            NettyConfig.setWriteBufferLowWaterMark(springBootNettyProperties.getWriteBufferLowWaterMark());
            NettyConfig.setAllowHalfClosure(springBootNettyProperties.getAllowHalfClosure());
            NettyConfig.setAutoRead(springBootNettyProperties.getAutoRead());
            NettyConfig.setSoBroadcast(springBootNettyProperties.getSoBroadcast());
            NettyConfig.setSoKeepalive(springBootNettyProperties.getSoKeepalive());
            NettyConfig.setSoSndbuf(springBootNettyProperties.getSoSndbuf());
            NettyConfig.setSoRcvbuf(springBootNettyProperties.getSoRcvbuf());
            NettyConfig.setSoReuseaddr(springBootNettyProperties.getSoReuseaddr());
            NettyConfig.setSoBacklog(springBootNettyProperties.getSoBacklog());
            NettyConfig.setIpTos(springBootNettyProperties.getIpTos());
            NettyConfig.setIpMulticastAddr(springBootNettyProperties.getIpMulticastAddr());
            NettyConfig.setIpMulticastIf(springBootNettyProperties.getIpMulticastIf());
            NettyConfig.setIpMulticastTtl(springBootNettyProperties.getIpMulticastTtl());
            NettyConfig.setIpMulticastLoopDisabled(springBootNettyProperties.getIpMulticastLoopDisabled());
            NettyConfig.setTcpNodelay(springBootNettyProperties.getTcpNodelay());
            NettyConfig.setSingleEventexecutorPerGroup(springBootNettyProperties.getSingleEventexecutorPerGroup());
            NettyConfig.setSoLinger(springBootNettyProperties.getSoLinger());
            NettyConfig.setSoTimeout(springBootNettyProperties.getSoTimeout());
        }


    }

}
