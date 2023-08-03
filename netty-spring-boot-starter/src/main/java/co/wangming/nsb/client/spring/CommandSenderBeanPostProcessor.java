package co.wangming.nsb.client.spring;

import co.wangming.nsb.client.command.CommandSender;
import co.wangming.nsb.client.command.CommandSenderExecutor;
import co.wangming.nsb.client.command.CommandTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created By WangMing On 2020-01-02
 **/
@Component
public class CommandSenderBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(CommandSenderBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class beanClass = bean.getClass();
        for (Field field : beanClass.getDeclaredFields()) {
            CommandSender commandSender = field.getAnnotation(CommandSender.class);
            if (commandSender == null) {
                continue;
            }

            try {
                field.setAccessible(true);
                CommandTemplate commandTemplate = (CommandTemplate) field.get(bean);
                field.setAccessible(false);
                CommandSenderExecutor.addCommand(() -> {
                    try {
                        commandTemplate.connect(commandSender.host(), commandSender.port());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                setGenericType(field, commandTemplate);
            } catch (Exception e) {
                log.error("", e);
            }
        }

        return bean;
    }

    private void setGenericType(Field field, CommandTemplate commandTemplate) {
        Type type = field.getGenericType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] args = parameterizedType.getActualTypeArguments();
            if (args == null || args.length == 0) {
                return;
            }

            try {
                Class<?> genericType = Class.forName(args[0].getTypeName());
                commandTemplate.settClass(genericType);
            } catch (ClassNotFoundException e) {
                log.error("CommandTemplate 设置类型信息, 找不到类: {}", args[0].getTypeName(), e);
            }
        }

    }

}