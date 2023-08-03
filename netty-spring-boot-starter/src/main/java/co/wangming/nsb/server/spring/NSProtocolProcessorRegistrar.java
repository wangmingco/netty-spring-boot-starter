package co.wangming.nsb.server.spring;

import co.wangming.nsb.common.spring.AbstractBeanDefinitionRegistrar;
import co.wangming.nsb.server.processors.NSProtocolProcessor;
import co.wangming.nsb.server.processors.ProtocolProcessorFactory;
import co.wangming.nsb.server.processors.ProtocolProcessorFactoryChain;
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
 * 用于扫描 #{@link NSProtocolProcessor} 注解
 * <p>
 * Created By WangMing On 2019-12-06
 **/
public class NSProtocolProcessorRegistrar extends AbstractBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(NSProtocolProcessorRegistrar.class);

    public List<Class> getAnnotationTypeFilterClass() {
        return Arrays.asList(NSProtocolProcessor.class);
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
        log.info("注册 NSProtocolProcessor");

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            try {
                Class<?> beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());

                NSProtocolProcessor NSProtocolProcessor = beanClass.getAnnotation(NSProtocolProcessor.class);
                if (NSProtocolProcessor == null) {
                    continue;
                }

                // 将 ProtocolProcessorFactory 添加到 ProtocolProcessorFactoryChain 里
                ProtocolProcessorFactoryChain.INSTANCE.addProtocolProcessorFactory((ProtocolProcessorFactory) beanClass.newInstance());
            } catch (ClassNotFoundException e) {
                log.error("寻找BeanDefinitionHolder时, 找不到类:{}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
                throw e;
            }
        }

    }

}
