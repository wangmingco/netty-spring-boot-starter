package co.wangming.nsb.samples;

import co.wangming.nsb.springboot.CommandScan;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created By WangMing On 2019-12-06
 **/
@SpringBootApplication
@CommandScan(basePackage = "co.wangming.nsb.samples")
@Slf4j
public class SocketServer {

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
