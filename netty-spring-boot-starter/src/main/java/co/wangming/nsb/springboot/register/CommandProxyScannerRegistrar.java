package co.wangming.nsb.springboot.register;

import co.wangming.nsb.command.CommandController;
import co.wangming.nsb.command.CommandMapping;
import co.wangming.nsb.command.CommandProxy;
import co.wangming.nsb.processors.ProtocolProcessor;
import co.wangming.nsb.processors.ProtocolProcessorRegister;
import co.wangming.nsb.processors.UnknowProtocolProcessor;
import co.wangming.nsb.util.CommandProxyMaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 用于扫描 #{@link CommandController} #{@link ProtocolProcessorRegister} 注解
 * <p>
 * Created By WangMing On 2019-12-06
 **/
public class CommandProxyScannerRegistrar extends AbstractCommandScannerRegistrar {

    private static final Logger log = LoggerFactory.getLogger(CommandProxyScannerRegistrar.class);

    private static List<Class> classes = new ArrayList() {{
        add(CommandController.class);
        add(ProtocolProcessorRegister.class);
    }};

    public List<Class> getAnnotationTypeFilterClass() {
        return classes;
    }

    private static class BeanDefinitionGroup {
        List<BeanWrapper> commandControllerList = new ArrayList<>();
        Map<Class, BeanWrapper> protocolProcessorRegisterList = new HashMap<>();
    }

    private static class BeanWrapper {
        Class beanClass;
        BeanDefinitionHolder beanDefinitionHolder;

        BeanWrapper(Class beanClass, BeanDefinitionHolder beanDefinitionHolder) {
            this.beanClass = beanClass;
            this.beanDefinitionHolder = beanDefinitionHolder;
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
        BeanDefinitionGroup beanDefinitionGroup = groupBeanDefinitionHolder(beanDefinitionHolders);

        for (BeanWrapper commandControllerBeanWrapper : beanDefinitionGroup.commandControllerList) {
            // 开始将 commandController 注册进Spring里
            for (Method method : commandControllerBeanWrapper.beanClass.getMethods()) {
                register(beanDefinitionRegistry, commandControllerBeanWrapper, method, beanDefinitionGroup.protocolProcessorRegisterList);
            }
        }
    }

    /**
     * 找到被 #{@link ProtocolProcessorRegister} 注解的参数解析器
     *
     * @param beanDefinitionHolders
     * @return
     */
    private BeanDefinitionGroup groupBeanDefinitionHolder(Set<BeanDefinitionHolder> beanDefinitionHolders) throws Exception {
//        Map<Class, Class> messageType2BeanClassMap = new HashMap<>();
        BeanDefinitionGroup beanDefinitionGroup = new BeanDefinitionGroup();

        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            try {
                Class<?> beanClass = Class.forName(beanDefinitionHolder.getBeanDefinition().getBeanClassName());

                ProtocolProcessorRegister protocolProcessorRegister = beanClass.getAnnotation(ProtocolProcessorRegister.class);
                if (protocolProcessorRegister != null) {
                    beanDefinitionGroup.protocolProcessorRegisterList.put(protocolProcessorRegister.messageType(), new BeanWrapper(beanClass, beanDefinitionHolder));
                }

                if (beanClass.getAnnotation(CommandController.class) != null) {
                    beanDefinitionGroup.commandControllerList.add(new BeanWrapper(beanClass, beanDefinitionHolder));
                }

            } catch (ClassNotFoundException e) {
                log.error("寻找BeanDefinitionHolder时, 找不到类:{}", beanDefinitionHolder.getBeanDefinition().getBeanClassName(), e);
                throw e;
            }
        }
        return beanDefinitionGroup;
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
    private void register(BeanDefinitionRegistry beanDefinitionRegistry, BeanWrapper commandControllerBeanWrapper,
                          Method method, Map<Class, BeanWrapper> protocolProcessorRegisterList) throws Exception {

        CommandMapping commandMappingAnnotation = method.getAnnotation(CommandMapping.class);
        // 不是消息处理方法, 则跳过处理
        if (commandMappingAnnotation == null) {
            return;
        }

        String beanName = commandControllerBeanWrapper.beanDefinitionHolder.getBeanName();

        String proxyClassName = CommandProxy.class.getSimpleName() + "$$" + commandMappingAnnotation.requestId();
        log.info("开始注册消息接口. beanName:{}, 代理类名:{}, 消息接口方法名称:{}", beanName, proxyClassName, method.getName());

        /**
         * 生成 #{@link CommandProxy} 代理类
         */
        Class commandProxyClass = CommandProxyMaker.INSTANCE.make(beanName, proxyClassName, commandControllerBeanWrapper.beanClass, method);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(commandProxyClass);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();

        MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();

        addMessageParser(beanDefinitionRegistry, mutablePropertyValues, method, protocolProcessorRegisterList);
        addMethodInfo(mutablePropertyValues, method, commandMappingAnnotation);

        beanDefinition.setPropertyValues(mutablePropertyValues);

        beanDefinitionRegistry.registerBeanDefinition(proxyClassName, beanDefinition);
    }

    /**
     * 找到方法参数的解析器
     *
     * @param method
     * @param protocolProcessorRegisterList
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void addMessageParser(BeanDefinitionRegistry beanDefinitionRegistry, MutablePropertyValues mutablePropertyValues, Method method, Map<Class, BeanWrapper> protocolProcessorRegisterList) throws IllegalAccessException, InstantiationException {

        List<ProtocolProcessor> protocolProcessors = new ArrayList<>();
        loop1:
        for (Class parameterType : method.getParameterTypes()) {

            for (Map.Entry<Class, BeanWrapper> parserRegisterEntry : protocolProcessorRegisterList.entrySet()) {
                Class messageType = parserRegisterEntry.getKey();
                if (messageType.isAssignableFrom(parameterType)) {
                    Class beanClass = parserRegisterEntry.getValue().beanClass;
                    ProtocolProcessor protocolProcessor = (ProtocolProcessor) beanClass.newInstance();
                    protocolProcessor.setParameterType(parameterType);
                    protocolProcessors.add(protocolProcessor);

                    // 提供给CommandTemplate使用
                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
                    AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
                    String protocolProcessorName = messageType.getSimpleName() + "ProtocolProcessor";
                    beanDefinitionRegistry.registerBeanDefinition(protocolProcessorName, beanDefinition);
                    continue loop1;
                }
            }

            UnknowProtocolProcessor unknowProtocolProcessor = new UnknowProtocolProcessor();
            unknowProtocolProcessor.setParameterType(parameterType);
            protocolProcessors.add(unknowProtocolProcessor);
        }

        mutablePropertyValues.add(CommandProxy.PARAMETER_PROCESSORS, protocolProcessors);

        Class<?> returnType = method.getReturnType();
        if (!Void.TYPE.equals(returnType)) {
            for (Map.Entry<Class, BeanWrapper> parserRegisterEntry : protocolProcessorRegisterList.entrySet()) {
                Class messageType = parserRegisterEntry.getKey();
                if (messageType.isAssignableFrom(returnType)) {
                    ProtocolProcessor protocolProcessor = (ProtocolProcessor) parserRegisterEntry.getValue().beanClass.newInstance();
                    protocolProcessor.setParameterType(returnType);
                    mutablePropertyValues.add(CommandProxy.RETURN_PROCESSOR, protocolProcessor);
                    break;
                }
            }
        }

    }

    private void addMethodInfo(MutablePropertyValues mutablePropertyValues, Method method, CommandMapping commandMappingAnnotation) {
        mutablePropertyValues.add(CommandProxy.REQUEST_ID, commandMappingAnnotation.requestId());
        mutablePropertyValues.add(CommandProxy.RESPONSE_ID, commandMappingAnnotation.responseId());
    }
}
