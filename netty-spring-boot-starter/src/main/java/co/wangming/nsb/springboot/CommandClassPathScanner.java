package co.wangming.nsb.springboot;

import co.wangming.nsb.netty.CommandController;
import co.wangming.nsb.netty.CommandMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * 将类路径上的被 #{@link CommandController} 注解的类定义找到
 * <p>
 * Created By WangMing On 2019-12-06
 **/
@Slf4j
public class CommandClassPathScanner extends ClassPathBeanDefinitionScanner {


    public CommandClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        log.debug("开始扫描包下所有BeanDefinitionHolder");

        addIncludeFilter(new AnnotationTypeFilter(CommandController.class));
        addIncludeFilter(new AnnotationTypeFilter(CommandMapping.class));

        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        log.debug("扫描包下所有BeanDefinitionHolder完成");

        return beanDefinitionHolders;
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        log.debug("isCandidateComponent --> {}", beanDefinition.getBeanClassName());

        return beanDefinition.getMetadata().getAnnotationTypes().contains(CommandController.class.getName()) ||
                beanDefinition.getMetadata().getAnnotationTypes().contains(CommandMapping.class.getName());
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        log.debug("checkCandidate --> {}", beanName);

        return super.checkCandidate(beanName, beanDefinition);
    }

}