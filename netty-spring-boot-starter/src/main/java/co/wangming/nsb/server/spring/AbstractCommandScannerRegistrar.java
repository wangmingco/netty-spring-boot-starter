package co.wangming.nsb.server.spring;

import co.wangming.nsb.server.exception.RegisterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created By WangMing On 2020-01-02
 **/
public abstract class AbstractCommandScannerRegistrar implements ResourceLoaderAware, ImportBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(AbstractCommandScannerRegistrar.class);

    protected ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * 将starter包加载到扫描器里, 新的需扫描路径只需要添加到 annotationPackages 全局变量里即可
     *
     * @param annotationMetadata
     * @return
     */
    protected String[] getScanPackages(AnnotationMetadata annotationMetadata) {
        //获取所有注解的属性和值
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(annotationMetadata.getAnnotationAttributes(CommandScan.class.getName()));
        //获取到basePackage的值
        String[] basePackages = annoAttrs.getStringArray("basePackage");
        //如果没有设置basePackage 扫描路径,就扫描对应包下面的值
        if (basePackages.length == 0) {
            basePackages = new String[]{((StandardAnnotationMetadata) annotationMetadata).getIntrospectedClass().getPackage().getName()};
        }

        List<String> scanPackages = new ArrayList<>();
        scanPackages.addAll(Arrays.asList(basePackages));
        for (Class annotationTypeFilterClass : getAnnotationTypeFilterClass()) {
            scanPackages.add(annotationTypeFilterClass.getPackage().getName());
        }

        String[] packages = new String[scanPackages.size()];
        for (int i = 0; i < scanPackages.size(); i++) {
            packages[i] = scanPackages.get(i);
        }
        return packages;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        log.debug("registerBeanDefinitions start: {}", annotationMetadata.getClassName());
        String[] scanPackages = getScanPackages(annotationMetadata);

        //自定义的包扫描器
        AbstractClassPathScanner commandClassPathScanner = new AbstractClassPathScanner(beanDefinitionRegistry, false);

        if (resourceLoader != null) {
            commandClassPathScanner.setResourceLoader(resourceLoader);
        }

        //这里实现的是根据名称来注入
        commandClassPathScanner.setBeanNameGenerator(new CommandNameGenerator());

        //扫描指定路径下的接口
        Set<BeanDefinitionHolder> beanDefinitionHolders = commandClassPathScanner.doScan(scanPackages);

        try {
            process(beanDefinitionRegistry, beanDefinitionHolders);
        } catch (final Exception e) {
            throw new RegisterException(e);
        }
    }

    public void process(BeanDefinitionRegistry beanDefinitionRegistry, Set<BeanDefinitionHolder> beanDefinitionHolders) throws Exception {

    }

    public class AbstractClassPathScanner extends ClassPathBeanDefinitionScanner {

        public AbstractClassPathScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
            super(registry, useDefaultFilters);
        }

        @Override
        public Set<BeanDefinitionHolder> doScan(String... basePackages) {
            for (Class annotationTypeFilterClass : getAnnotationTypeFilterClass()) {
                addIncludeFilter(new AnnotationTypeFilter(annotationTypeFilterClass, true, true));
            }
            return super.doScan(basePackages);
        }

    }

    public abstract List<Class> getAnnotationTypeFilterClass();
}
