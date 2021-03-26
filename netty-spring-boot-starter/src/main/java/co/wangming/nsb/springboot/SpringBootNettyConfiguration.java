package co.wangming.nsb.springboot;

import co.wangming.nsb.netty.server.NettyServer;
import co.wangming.nsb.springboot.BeanPostProcessor.CommandSenderBeanPostProcessor;
import co.wangming.nsb.springboot.factorybean.CommandSenderFactoryBean;
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

import javax.annotation.Resource;

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

        @Resource
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
