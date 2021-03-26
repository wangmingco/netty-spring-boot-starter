package co.wangming.nsb.springboot;

import co.wangming.nsb.command.CommandController;
import co.wangming.nsb.command.CommandMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * Created By WangMing On 2019-12-07
 **/
public class CommandNameGenerator extends AnnotationBeanNameGenerator {

    private static final Logger log = LoggerFactory.getLogger(CommandNameGenerator.class);

    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
        //从自定义注解中拿name
        String name = getNameByServiceFindAnntation(definition, registry);
        if (name != null) {
            return name;
        }
        //走父类的方法
        return super.generateBeanName(definition, registry);
    }

    private String getNameByServiceFindAnntation(BeanDefinition definition, BeanDefinitionRegistry registry) {
        String beanClassName = definition.getBeanClassName();
        try {
            Class<?> aClass = Class.forName(beanClassName);
            CommandController commandController = aClass.getAnnotation(CommandController.class);
            if (commandController != null) {
                String className = aClass.getSimpleName();
                className = className.substring(0, 1).toLowerCase() + className.substring(1);
                return className;
            }
            CommandMapping commandMapping = aClass.getAnnotation(CommandMapping.class);
            if (commandMapping != null) {
                return beanClassName;
            }
            //获取到注解name的值并返回
            return null;
        } catch (ClassNotFoundException e) {
            log.error("getNameByServiceFindAnntation error:{}", beanClassName, e);
            return null;
        }
    }

}
