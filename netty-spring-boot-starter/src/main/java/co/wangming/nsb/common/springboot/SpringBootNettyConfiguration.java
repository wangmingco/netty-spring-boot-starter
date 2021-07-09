package co.wangming.nsb.common.springboot;

import co.wangming.nsb.client.spring.CommandSenderBeanPostProcessor;
import co.wangming.nsb.client.spring.CommandSenderFactoryBean;
import co.wangming.nsb.common.spring.SpringContext;
import co.wangming.nsb.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * Created By WangMing On 2019-12-06
 **/
@Configuration
@EnableConfigurationProperties(SpringBootNettyProperties.class)
public class SpringBootNettyConfiguration {

    private static final Logger log = LoggerFactory.getLogger(SpringBootNettyConfiguration.class);

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

    @Bean
    @ConditionalOnMissingBean(CommandSenderBeanPostProcessor.class)
    public CommandSenderBeanPostProcessor commandSenderBeanPostProcessor() {
        return new CommandSenderBeanPostProcessor();
    }

    @Bean
    @ConditionalOnMissingBean(CommandSenderFactoryBean.class)
    public CommandSenderFactoryBean commandSenderFactoryBean() {
        return new CommandSenderFactoryBean();
    }

    @Component
    public static class NettyStarter implements InitializingBean, DisposableBean {

        @Autowired
        private SpringBootNettyProperties springBootNettyProperties;

        private NettyServer nettyServer;

        @Override
        public void afterPropertiesSet() throws Exception {
            log.info("Starting The Netty Server");

            NettyServer nettyServer = new NettyServer();

            nettyServer.start(springBootNettyProperties);

            this.nettyServer = nettyServer;
            log.info("Started The Netty Server");
        }

        @Override
        public void destroy() throws Exception {
            log.info("Stopping The Netty Server");

            nettyServer.stop();

            log.info("Stopped The Netty Server");
        }

    }

}
