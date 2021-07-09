package co.wangming.nsb.samples;

import co.wangming.nsb.common.spring.RegistrarScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created By WangMing On 2019-12-06
 **/
@SpringBootApplication
@RegistrarScan(basePackage = "co.wangming.nsb.samples")
public class SocketServer {

    private static final Logger log = LoggerFactory.getLogger(SocketServer.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SocketServer.class);

        log.info("********************************************************************");
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {

            Object bean = context.getBean(beanDefinitionName);
            String name = bean.getClass().getCanonicalName();
            if (name == null) {
                log.error("找不到类名:{}", beanDefinitionName);
                continue;
            }
            if (!name.contains("wangming")) {
                continue;
            }
            log.info("Find  --> " + beanDefinitionName + " ---  " + bean);
        }
        log.info("********************************************************************");
    }
}
