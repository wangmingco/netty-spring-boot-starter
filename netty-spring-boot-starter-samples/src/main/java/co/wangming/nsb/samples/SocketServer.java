package co.wangming.nsb.samples;

import co.wangming.nsb.springboot.CommandScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created By WangMing On 2019-12-06
 **/
@SpringBootApplication
@CommandScan(basePackage = "co.wangming.nsb.samples")
public class SocketServer {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SocketServer.class);

        System.out.println("********************************************************************");
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {

            Object bean = context.getBean(beanDefinitionName);
            if (!bean.getClass().getCanonicalName().contains("wangming")) {
                continue;
            }
            System.out.println("Find  --> " + beanDefinitionName + " ---  " + bean);
        }
        System.out.println("********************************************************************");
    }
}
