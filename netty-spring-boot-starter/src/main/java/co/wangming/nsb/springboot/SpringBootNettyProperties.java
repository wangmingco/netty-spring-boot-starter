package co.wangming.nsb.springboot;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created By WangMing On 2019-12-06
 **/
@ConfigurationProperties(prefix = "springboot.netty")
public class SpringBootNettyProperties {

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
