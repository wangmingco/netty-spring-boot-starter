package co.wangming.nsb.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created By WangMing On 2019-12-07
 **/
@Slf4j
public class SpringContext implements ApplicationContextAware {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static <T> T getBean(Class<T> requiredType) throws BeansException {
        return context.getBean(requiredType);
    }

    public static Object getBean(String requiredType) {
        return context.getBean(requiredType);
    }


}
