package co.wangming.nsb.rpc.springboot;

import co.wangming.nsb.rpc.utils.NSService;
import co.wangming.nsb.rpc.utils.NSType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Created by wangming on 2017/6/3.
 */
@Component
@Slf4j
public class NSBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final String SERVICE_PACKAGE = "com.nscall.springboot.service";

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        ClassPathMapperScanner classPathMapperScanner = new ClassPathMapperScanner(registry);
        classPathMapperScanner.scan(SERVICE_PACKAGE);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

    public static class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {
        private static NSClassGenerator.NSClassLoader rpcClassLoader = new NSClassGenerator.NSClassLoader();
        private NSClassGenerator nsClassGenerator = new NSClassGenerator();

        public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        public int scan(String... basePackages) {
            return super.scan(basePackages);
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);//首先调用Spring默认的扫描装配操作
            for (BeanDefinitionHolder holder : beanDefinitions) {//然后循环对每一个BeanDefinition进行一些自定义的操作
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
                try {
                    Class<?> beanClass = loadClass(definition.getBeanClassName());
                    Class clazz = nsClassGenerator.implemetsInterface(beanClass);
                    definition.setBeanClass(clazz);
                } catch (ClassNotFoundException e) {
                    log.error("", e);
                }
            }

            return beanDefinitions;
        }

        private Class<?> loadClass(String beanClassName) throws ClassNotFoundException {
            return rpcClassLoader.loadClass(beanClassName);
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            try {
                Class<?> beanClass = loadClass(beanDefinition.getBeanClassName());
                NSService anno = beanClass.getAnnotation(NSService.class);
                if (anno == null) {
                    return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
                } else {
                    NSType callType = anno.rpcCallType();
                    switch (callType) {
                        case LOCAL: {
                            return true;
                        }
                        case REMOTE: {
                            return true;
                        }
                        default: {
                            return false;
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("", e);
                return false;
            }
        }
    }
}
