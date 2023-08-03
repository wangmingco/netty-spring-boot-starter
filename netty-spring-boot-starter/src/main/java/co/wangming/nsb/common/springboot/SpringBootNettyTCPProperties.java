package co.wangming.nsb.common.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 *
 * @author ming.wang
 * @date 2023/8/3
 */
@ConfigurationProperties(prefix = "spring.boot.netty.tcp")
public class SpringBootNettyTCPProperties extends SpringBootNettyProperties{

    private Integer port = 7001;

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public void setPort(Integer port) {
        this.port = port;
    }
}
