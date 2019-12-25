package co.wangming.nsb.springboot;

import co.wangming.nsb.command.CommandController;
import co.wangming.nsb.command.CommandMapping;
import co.wangming.nsb.command.CommandProxy;
import co.wangming.nsb.exception.RegisterException;
import co.wangming.nsb.processors.MethodProtocolProcessor;
import co.wangming.nsb.processors.ProtocolProcessorRegister;
import co.wangming.nsb.processors.UnknowProtocolProcessor;
import co.wangming.nsb.util.ProxyClassMaker;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.MutablePropertyValues;
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
        add(ProtocolProcessorRegister.class.getPackage().getName());
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

        try {
            registerCommandMapping(beanDefinitionRegistry, beanDefinitionHolders);
        } catch (final Exception e) {
            throw new RegisterException(e);
        }
    }

    /**
     * 将starter包加载到扫描器里, 新的需扫描路径只需要添加到 annotationPackages 全局变量里即可
     *
     * @param annotationMetadata
     * @return
     */
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

    /**
     * 进bean注册
     *
     * @param beanDefinitionRegistry
     * @param beanDefinitionHolders
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private void registerCommandMapping(BeanDefinitionRegistry beanDefinitionRegistry, Set<BeanDefinitionHolder> beanDefinitionHolders) throws Exception {
        Map<Class, Class> registerMessageType2BeanClassMap = getParserRegisters(beanDefinitionHolders);

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {

            String beanClassName = beanDefinitionHolder.getBeanDefinition().getBeanClassName();

            Class beanClass = null;
            try {
                beanClass = Class.forName(beanClassName);
            } catch (ClassNotFoundException e) {
                log.error("加载类失败:{}", beanClassName, e);
                throw e;
            }

            log.info("开始加载消息类:[{}] 中的消息接口", beanClassName);

            for (Method method : beanClass.getMethods()) {
                CommandMapping commandMappingAnnotation = method.getAnnotation(CommandMapping.class);
                // 不是消息处理方法, 则跳过处理
                if (commandMappingAnnotation == null) {
                    continue;
                }

                register(beanDefinitionRegistry, beanDefinitionHolder.getBeanName(), beanClass, method, commandMappingAnnotation, registerMessageType2BeanClassMap);
            }
        }
    }

    /**
     * 找到被 #{@link ProtocolProcessorRegister} 注解的参数解析器
     *
     * @param beanDefinitionHolders
     * @return
     */
    private Map<Class, Class> getParserRegisters(Set<BeanDefinitionHolder> beanDefinitionHolders) throws Exception {
        Map<Class, Class> messageType2BeanClassMap = new HashMap<>();

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            try {
                Class<?> beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());
                if (MethodProtocolProcessor.class.isAssignableFrom(beanClass)) {
                    ProtocolProcessorRegister protocolProcessorRegister = beanClass.getAnnotation(ProtocolProcessorRegister.class);
                    messageType2BeanClassMap.put(protocolProcessorRegister.messageType(), beanClass);

                    log.info("找到ParserRegister, 所在类:{}, 消息类型:{}", beanClass.getName(), protocolProcessorRegister.messageType());
                }
            } catch (ClassNotFoundException e) {
                log.error("寻找ParserRegister时, 找不到类:{}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
                throw e;
            }
        }
        return messageType2BeanClassMap;
    }

    /**
     * 将每个消息方法都生成代理类注册到Spring里
     *
     * 扫描到了被 #{@link CommandController} 注解的类, 但是还是需要将该类里面的被 #{@link CommandMapping} 注解的方法处理一下.
     *
     * 当前的背景是, 要将netty收到的消息转发到该方法上同时带上spring整个环境. 目前的做法是要将每个方法生成一个代理类, 代理类里直接调用
     * 被 #{@link CommandMapping} 注解的方法.
     *
     * @param beanDefinitionRegistry
     * @param beanName
     * @param beanClass
     * @param method
     * @param commandMappingAnnotation
     */
    private void register(BeanDefinitionRegistry beanDefinitionRegistry, String beanName, Class beanClass,
                          Method method, CommandMapping commandMappingAnnotation, Map<Class, Class> registerMessageType2BeanClassMap) throws InstantiationException, IllegalAccessException {

        String proxyClassName = CommandProxy.class.getSimpleName() + "$$" + commandMappingAnnotation.id();
        log.info("开始注册消息接口. beanName:{}, 代理类名:{}, 消息接口方法名称:{}", beanName, proxyClassName, method.getName());

        Class proxyClass = ProxyClassMaker.make(beanName, proxyClassName, beanClass, method);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(proxyClass);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        addMessageParser(beanDefinition, method, registerMessageType2BeanClassMap);

        beanDefinitionRegistry.registerBeanDefinition(proxyClassName, beanDefinition);
    }

    /**
     * 找到方法参数的解析器
     *
     * @param method
     * @param registerMessageType2BeanClassMap
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void addMessageParser(AbstractBeanDefinition beanDefinition, Method method, Map<Class, Class> registerMessageType2BeanClassMap) throws IllegalAccessException, InstantiationException {
        List<MethodProtocolProcessor> methodProtocolProcessors = new ArrayList<>();
        loop1:
        for (Class parameterType : method.getParameterTypes()) {

            for (Map.Entry<Class, Class> parserRegisterEntry : registerMessageType2BeanClassMap.entrySet()) {
                Class messageType = parserRegisterEntry.getKey();
                if (messageType.isAssignableFrom(parameterType)) {
                    MethodProtocolProcessor methodProtocolProcessor = (MethodProtocolProcessor) parserRegisterEntry.getValue().newInstance();
                    methodProtocolProcessor.setParameterType(parameterType);
                    methodProtocolProcessors.add(methodProtocolProcessor);
                    continue loop1;
                }
            }

            UnknowProtocolProcessor unknowProtocolProcessor = new UnknowProtocolProcessor();
            unknowProtocolProcessor.setParameterType(parameterType);
            methodProtocolProcessors.add(unknowProtocolProcessor);
        }

        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        mutablePropertyValues.add(CommandProxy.PARAMETER_PROCESSORS, methodProtocolProcessors);

        Class<?> returnType = method.getReturnType();
        if (!Void.TYPE.equals(returnType)) {
            for (Map.Entry<Class, Class> parserRegisterEntry : registerMessageType2BeanClassMap.entrySet()) {
                Class messageType = parserRegisterEntry.getKey();
                if (messageType.isAssignableFrom(returnType)) {
                    MethodProtocolProcessor methodProtocolProcessor = (MethodProtocolProcessor) parserRegisterEntry.getValue().newInstance();
                    methodProtocolProcessor.setParameterType(returnType);
                    mutablePropertyValues.add(CommandProxy.RETURN_PROCESSOR, methodProtocolProcessor);
                    break;
                }
            }
        }

        beanDefinition.setPropertyValues(mutablePropertyValues);
        String parserNames = methodProtocolProcessors.stream().map(it -> it.getClass().getSimpleName()).collect(Collectors.joining(","));
        log.info("代理类:{} 添加MessageParser:{}", beanDefinition.getBeanClassName(), parserNames);
    }
}
