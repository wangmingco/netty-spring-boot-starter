package co.wangming.nsb.springboot;

import co.wangming.nsb.command.CommandController;
import co.wangming.nsb.command.CommandMapping;
import co.wangming.nsb.command.CommandMethod;
import co.wangming.nsb.command.CommandMethodCache;
import co.wangming.nsb.netty.CommandProxy;
import co.wangming.nsb.parsers.CommonParser;
import co.wangming.nsb.parsers.MessageParser;
import co.wangming.nsb.parsers.ParserRegister;
import co.wangming.nsb.util.ProxyClassMaker;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用于扫描 #{@link CommandController} 注解
 * <p>
 * Created By WangMing On 2019-12-06
 **/
@Slf4j
public class CommandScannerRegistrar implements ResourceLoaderAware, ImportBeanDefinitionRegistrar {

    private static final List<String> annotationPackages = new ArrayList() {{
        add(ParserRegister.class.getPackage().getName());
    }};

    private ResourceLoader resourceLoader;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry beanDefinitionRegistry) {

        log.debug("registerBeanDefinitions start: {}", annotationMetadata.getClassName());
        String[] scanPackages = getScanPackages(annotationMetadata);

        //自定义的包扫描器
        CommandClassPathScanner commandClassPathScanner = new CommandClassPathScanner(beanDefinitionRegistry, false);

        if (resourceLoader != null) {
            commandClassPathScanner.setResourceLoader(resourceLoader);
        }

        //这里实现的是根据名称来注入
        commandClassPathScanner.setBeanNameGenerator(new CommandNameGenerator());

        log.info("commandClassPathScanner 扫描路径:{}", JSON.toJSONString(scanPackages, true));

        //扫描指定路径下的接口
        Set<BeanDefinitionHolder> beanDefinitionHolders = commandClassPathScanner.doScan(scanPackages);

        String beanNames = beanDefinitionHolders.stream().map(it -> it.getBeanName()).collect(Collectors.joining(", "));
        log.info("commandClassPathScanner 扫描到的bean名称:{}", beanNames);

        try {
            registerCommandMapping(beanDefinitionRegistry, beanDefinitionHolders);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private String[] getScanPackages(AnnotationMetadata annotationMetadata) {
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
        scanPackages.addAll(annotationPackages);

        String[] packages = new String[scanPackages.size()];
        for (int i = 0; i < scanPackages.size(); i++) {
            packages[i] = scanPackages.get(i);
        }
        return packages;
    }

    private void registerCommandMapping(BeanDefinitionRegistry beanDefinitionRegistry, Set<BeanDefinitionHolder> beanDefinitionHolders) throws InstantiationException, IllegalAccessException {
        Map<Class, Class> parserComponets = getParserComponets(beanDefinitionHolders);

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            log.debug("Find BeanDefinitionHolder: {}", beanDefinitionHolder.getBeanName());

            String beanClassName = beanDefinitionHolder.getBeanDefinition().getBeanClassName();

            Class beanClass = null;
            try {
                beanClass = Class.forName(beanClassName);
            } catch (ClassNotFoundException e) {
                log.error("", e);
                continue;
            }

            for (Method method : beanClass.getMethods()) {
                CommandMapping commandMappingAnnotation = method.getAnnotation(CommandMapping.class);
                // 不是消息接受类, 则跳过处理
                if (commandMappingAnnotation == null) {
                    continue;
                }

                // 拿到参数信息
                List<MessageParser> parameterInfoList = handleParameter(method, parserComponets);

                CommandMethod commandMethod = CommandMethod.builder()
                        .messageParsers(parameterInfoList)
                        .beanName(beanDefinitionHolder.getBeanName())
                        .build();

                CommandMethodCache.add(String.valueOf(commandMappingAnnotation.id()), commandMethod);

                log.info("消息[{}] 注册CommandMethod:{}", commandMappingAnnotation.id(), commandMethod);

                register(beanDefinitionRegistry, beanDefinitionHolder.getBeanName(), beanClass, method, commandMappingAnnotation);
            }
        }
    }

    private Map<Class, Class> getParserComponets(Set<BeanDefinitionHolder> beanDefinitionHolders) {
        Map<Class, Class> map = new HashMap<>();

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            try {
                Class<?> beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
                if (MessageParser.class.isAssignableFrom(beanClass)) {
                    ParserRegister parserRegister = beanClass.getAnnotation(ParserRegister.class);
                    map.put(parserRegister.messageType(), beanClass);

                    log.info("找到ParserComponets: {} -> {}", parserRegister.messageType(), beanClass.getName());
                }
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }
        }
        return map;
    }


    private List<MessageParser> handleParameter(Method method, Map<Class, Class> parserComponets) throws IllegalAccessException, InstantiationException {
        List<MessageParser> parameterInfoList = new ArrayList<>();
        loop1:
        for (Class parameterType : method.getParameterTypes()) {

            for (Map.Entry<Class, Class> parserComponetEntry : parserComponets.entrySet()) {
                if (parserComponetEntry.getKey().isAssignableFrom(parameterType)) {
                    MessageParser messageParser = (MessageParser) parserComponetEntry.getValue().newInstance();
                    messageParser.setParser(parameterType);
                    parameterInfoList.add(messageParser);
                    continue loop1;
                }
            }

            parameterInfoList.add(new CommonParser());
        }

        return parameterInfoList;
    }

    private void register(BeanDefinitionRegistry beanDefinitionRegistry, String beanName, Class beanClass, Method method, CommandMapping commandMappingAnnotation) {
        String commandMappingName = beanName + "$$" + CommandProxy.class.getSimpleName() + "$$" + commandMappingAnnotation.id();
        String proxyClassName = beanClass.getCanonicalName() + "$$" + CommandProxy.class.getSimpleName() + "$$" + commandMappingAnnotation.id();
        log.debug(" 注册beanName:{} 的代理beanName:{}", beanName, commandMappingName);

        /**
         * 现在扫描到了被 #{@link CommandController} 注解的类, 但是还是需要将该类里面的被 #{@link CommandMapping} 注解的方法处理一下.
         *
         * 当前的背景是, 要将netty收到的消息转发到该方法上同时带上spring整个环境. 目前的做法是要将每个方法生成一个代理类, 代理类里直接调用
         * 被 #{@link CommandMapping} 注解的方法.
         */
        Class proxyClass = ProxyClassMaker.make(beanName, proxyClassName, beanClass, method);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(proxyClass);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        beanDefinitionRegistry.registerBeanDefinition(commandMappingName, beanDefinition);
    }
}
