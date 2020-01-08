package co.wangming.nsb.springboot.BeanPostProcessor;

import co.wangming.nsb.command.CommandSender;
import co.wangming.nsb.netty.client.CommandTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created By WangMing On 2020-01-02
 **/
@Slf4j
@Component
public class CommandSenderBeanPostProcessor implements BeanPostProcessor {

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
            CommandTemplate commandTemplate = null;
            try {
                field.setAccessible(true);
                commandTemplate = (CommandTemplate) field.get(bean);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                log.error("", e);
            }

            setHostAndPort(commandSender, commandTemplate);

            setGenericType(field, commandTemplate);
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
                commandTemplate.setTClass(genericType);
            } catch (ClassNotFoundException e) {
                log.error("", e);
            }
        }

    }

    private void setHostAndPort(CommandSender commandSender, CommandTemplate commandTemplate) {
        String host = commandSender.host();
        int port = commandSender.port();

        if (StringUtils.isNotEmpty(host) && port > 0) {
            commandTemplate.setHost(host);
            commandTemplate.setPort(port);
        }
    }
}