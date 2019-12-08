package co.wangming.nsb.springboot;

import co.wangming.nsb.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
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
    public class NettyStarter implements CommandLineRunner {

        @Resource
        private SpringBootNettyProperties springBootNettyProperties;

        @Override
        public void run(String... args) throws Exception {

            log.debug("Start The Netty Server");

            NettyServer nettyServer = NettyServer.builder()
                    .port(springBootNettyProperties.getPort())
                    .build();

            nettyServer.start();
        }

    }
}
