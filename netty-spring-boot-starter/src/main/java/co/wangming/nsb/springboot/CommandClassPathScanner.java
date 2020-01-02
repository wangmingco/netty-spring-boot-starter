package co.wangming.nsb.springboot;

import co.wangming.nsb.command.CommandController;
import co.wangming.nsb.command.ScannedCommand;
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
        addIncludeFilter(new AnnotationTypeFilter(ScannedCommand.class, true, true));
        // 目前只用 ScannedCommand 进行扫描, 后期重新规划, 看是继续使用 AnnotationTypeFilter 还是使用 AssignableTypeFilter
//        addIncludeFilter(new AssignableTypeFilter(MethodProtocolProcessor.class));
//        addIncludeFilter(new AssignableTypeFilter(EventHandler.class));
        return super.doScan(basePackages);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition);
    }

    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
        return super.checkCandidate(beanName, beanDefinition);
    }

}