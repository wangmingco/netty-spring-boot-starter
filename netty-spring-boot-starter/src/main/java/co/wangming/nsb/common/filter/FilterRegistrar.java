package co.wangming.nsb.common.filter;

import co.wangming.nsb.common.spring.AbstractBeanDefinitionRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 用于扫描 #{@link Filter} 注解
 * <p>
 * Created By WangMing On 2023-08-11
 **/
public class FilterRegistrar extends AbstractBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(FilterRegistrar.class);

    public List<Class> getAnnotationTypeFilterClass() {
        return Arrays.asList(Filter.class);
    }

    @Override
    public BeanNameGenerator beanNameGenerator() {
        return new AnnotationBeanNameGenerator();
    }

    /**
     * 进bean注册
     *
     * @param beanDefinitionRegistry
     * @param beanDefinitionHolders
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Override
    public void process(BeanDefinitionRegistry beanDefinitionRegistry, Set<BeanDefinitionHolder> beanDefinitionHolders) throws Exception {

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            try {
                Class<?> beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());

                Filter filter = beanClass.getAnnotation(Filter.class);
                if (filter == null) {
                    continue;
                }
                Class<AbstractFilter> filterBeanClass = (Class<AbstractFilter>)beanClass;
                FilterChain.INSTANCE.addFilter(filter, filterBeanClass);
            } catch (ClassNotFoundException e) {
                throw e;
            }
        }

    }

}
