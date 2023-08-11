package co.wangming.nsb.server.spring;

import co.wangming.nsb.common.spring.AbstractBeanDefinitionRegistrar;
import co.wangming.nsb.server.command.CommandController;
import co.wangming.nsb.server.command.CommandMapping;
import co.wangming.nsb.server.command.CommandProxy;
import co.wangming.nsb.server.command.CommandProxyMaker;
import co.wangming.nsb.server.processors.ProtocolProcessor;
import co.wangming.nsb.server.processors.ProtocolProcessorFactoryChain;
import co.wangming.nsb.server.processors.UnknowProtocolProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用于扫描 #{@link CommandController} 注解
 * <p>
 * Created By WangMing On 2019-12-06
 **/
public class CommandControllerRegistrar extends AbstractBeanDefinitionRegistrar {

    private static final Logger log = LoggerFactory.getLogger(CommandControllerRegistrar.class);

    @Override
    public List<Class> getAnnotationTypeFilterClass() {
        return Arrays.asList(CommandController.class);
    }

    @Override
    public BeanNameGenerator beanNameGenerator() {
        return new CommandNameGenerator();
    }

    public static class CommandNameGenerator extends AnnotationBeanNameGenerator {

        private static final Logger log = LoggerFactory.getLogger(CommandNameGenerator.class);

        @Override
        public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {
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
                return super.generateBeanName(definition, registry);
            } catch (ClassNotFoundException e) {
                log.error("getNameByServiceFindAnntation error:{}", beanClassName, e);
                //走父类的方法
                return super.generateBeanName(definition, registry);
            }
        }

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
        log.info("注册 CommandController ");

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            try {
                Class<?> beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());

                if (beanClass.getAnnotation(CommandController.class) == null) {
                    continue;
                }

                for (Method method : beanClass.getMethods()) {
                    // 开始将 commandController 注册进Spring里
                    register(beanDefinitionRegistry, beanClass, beanDefinitionHolder, method);
                }
            } catch (ClassNotFoundException e) {
                log.error("寻找BeanDefinitionHolder时, 找不到类:{}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
                throw e;
            }
        }

    }

    /**
     * 将每个消息方法都生成代理类注册到Spring里
     * <p>
     * 扫描到了被 #{@link CommandController} 注解的类, 但是还是需要将该类里面的被 #{@link CommandMapping} 注解的方法处理一下.
     * <p>
     * 当前的背景是, 要将netty收到的消息转发到该方法上同时带上spring整个环境. 目前的做法是要将每个方法生成一个代理类, 代理类里直接调用
     * 被 #{@link CommandMapping} 注解的方法.
     *
     * @param beanDefinitionRegistry
     * @param method
     */
    private void register(BeanDefinitionRegistry beanDefinitionRegistry, Class beanClass, BeanDefinitionHolder beanDefinitionHolder, Method method) throws Exception {

        CommandMapping commandMappingAnnotation = method.getAnnotation(CommandMapping.class);
        // 不是消息处理方法, 则跳过处理
        if (commandMappingAnnotation == null) {
            return;
        }

        String beanName = beanDefinitionHolder.getBeanName();
        String commandProxyClassName = CommandProxy.class.getCanonicalName() + "$$" + commandMappingAnnotation.requestId();

        try{
            /**
             * 生成 #{@link CommandProxy} 实现类
             */
            Class commandProxyClass = CommandProxyMaker.INSTANCE.make(beanName, commandProxyClassName, CommandProxy.lookup(), beanClass, method);

            BeanDefinitionBuilder commandProxyBuilder = BeanDefinitionBuilder.genericBeanDefinition(commandProxyClass);
            AbstractBeanDefinition commandProxyBeanDefinition = commandProxyBuilder.getBeanDefinition();
            MutablePropertyValues propertyValues = commandProxyFields(commandMappingAnnotation, method);
            propertyValues.add(CommandProxy.TARGET_CLASS, beanClass);
            propertyValues.add(CommandProxy.TARGET_METHOD, method);
            commandProxyBeanDefinition.setPropertyValues(propertyValues);
            beanDefinitionRegistry.registerBeanDefinition(commandProxyClassName, commandProxyBeanDefinition);

            List<ProtocolProcessor> protocolProcessors = (List<ProtocolProcessor>)propertyValues.getPropertyValue(CommandProxy.PARAMETER_PROCESSORS).getValue();
            String collect = protocolProcessors.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.joining(", "));
            log.info("注册消息接口完成. \n**************************\n  beanName: {}, \n  代理类: {}, \n  消息接口方法: {}, \n  目标类: {}, \n  处理器: {}\n**************************",
                    beanName, commandProxyClassName, method.getName(), beanClass.getCanonicalName(), collect);
        } catch (Exception e) {
            log.error("注册消息接口失败. \n**************************\n  beanName: {}, \n  代理类: {}, \n  消息接口方法: {}, \n  目标类: {}\n**************************",
                    beanName, commandProxyClassName, method.getName(), beanClass.getSimpleName(), e);
            throw e;
        }

    }

    private MutablePropertyValues commandProxyFields(CommandMapping commandMappingAnnotation, Method method) {
        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
        /**
         * 为 #{@link CommandProxy#requestId} 和 #{@link CommandProxy#responseId} 设置值
         */
        mutablePropertyValues.add(CommandProxy.REQUEST_ID, commandMappingAnnotation.requestId());
        mutablePropertyValues.add(CommandProxy.RESPONSE_ID, commandMappingAnnotation.responseId());

        /**
         * 为 #{@link CommandProxy#parameterProtocolProcessors} 设置值，即设置参数处理器
         */
        List<ProtocolProcessor> protocolProcessors = new ArrayList<>();
        for (Class parameterType : method.getParameterTypes()) {

            ProtocolProcessor protocolProcessor = ProtocolProcessorFactoryChain.INSTANCE.getProtocolProcessor(parameterType);
            if (protocolProcessor == null) {
                UnknowProtocolProcessor unknowProtocolProcessor = new UnknowProtocolProcessor();
                unknowProtocolProcessor.setParameterType(parameterType);
                protocolProcessors.add(unknowProtocolProcessor);
            } else {
                protocolProcessors.add(protocolProcessor);
            }

        }
        mutablePropertyValues.add(CommandProxy.PARAMETER_PROCESSORS, protocolProcessors);

        /**
         * 为 #{@link CommandProxy#returnProtocolProcessor} 设置值，即设置出参处理器
         */
        Class<?> returnType = method.getReturnType();
        if (!Void.TYPE.equals(returnType)) {
            ProtocolProcessor protocolProcessor = ProtocolProcessorFactoryChain.INSTANCE.getProtocolProcessor(returnType);
            mutablePropertyValues.add(CommandProxy.RETURN_PROCESSOR, protocolProcessor);
        }

        return mutablePropertyValues;
    }

}
