package co.wangming.nsb.springboot;

import co.wangming.nsb.command.CommandController;
import co.wangming.nsb.command.CommandMapping;
import co.wangming.nsb.parsers.ParserRegister;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 将类路径上的被 #{@link CommandController} 注解的类定义找到
 * <p>
 * Created By WangMing On 2019-12-06
 **/
@Slf4j
public class CommandClassPathScanner extends ClassPathBeanDefinitionScanner {

    private static final List<Class> annotations = new ArrayList() {{
        add(CommandController.class);
        add(CommandMapping.class);
        add(ParserRegister.class);
    }};

    public CommandClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        log.debug("开始扫描包下所有BeanDefinitionHolder");

        for (Class annotation : annotations) {
            addIncludeFilter(new AnnotationTypeFilter(annotation));
        }

        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        log.debug("扫描包下所有BeanDefinitionHolder完成");

        return beanDefinitionHolders;
    }


    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        log.debug("isCandidateComponent --> {}", beanDefinition.getBeanClassName());

        Set<String> annotationTypes = beanDefinition.getMetadata().getAnnotationTypes();
        for (Class annotation : annotations) {
            addIncludeFilter(new AnnotationTypeFilter(annotation));
            if (annotationTypes.contains(annotation.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        log.debug("checkCandidate --> {}", beanName);

        return super.checkCandidate(beanName, beanDefinition);
    }

}