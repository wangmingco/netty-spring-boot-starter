package co.wangming.nsb.springboot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.annotation.Annotation;
import java.util.Map;

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

    public static <T> Map<String, T> getBeansOfType(Class<T> requiredType) throws BeansException {
        return context.getBeansOfType(requiredType);
    }

    public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> requiredType) throws BeansException {
        return context.getBeansWithAnnotation(requiredType);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        return context.getBean(name, requiredType);
    }
}
