package co.wangming.nsb.netty;

import co.wangming.nsb.constant.MessageType;
import co.wangming.nsb.springboot.SpringContext;
import co.wangming.nsb.util.CommandMethodCache;
import co.wangming.nsb.vo.MethodInfo;
import co.wangming.nsb.vo.ParameterInfo;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By WangMing On 2019-12-07
 **/
@Slf4j
public class CommandDispatcher {

    public static void dispatch(ChannelHandlerContext ctx, MessageType messageType, byte[] messageBytes) {
        MethodInfo methodInfo = CommandMethodCache.getMethodInfo(String.valueOf(messageType.getType()));

        List<ParameterInfo> parameterInfoList = methodInfo.getParameterInfoList();
        Class targetBeanClass = methodInfo.getTargetBeanClass();
        Method targetMethod = methodInfo.getTargetMethod();

        // 生成调用方法参数
        List paramters = getParameters(ctx, messageType, messageBytes, parameterInfoList);

        // 调用方法
        Object result = invoke(targetBeanClass, targetMethod, paramters);

        // 调用方法后可能产生应答, 将应答返回给前端
        response(ctx, messageType, result);
    }

    /**
     * 生成调用  #{@link CommandMapping} 方法的参数, 主要是根据 #{@link MessageType} 进行解析.
     * 目前只支持Protobuf参数和 #{@link ChannelHandlerContext}
     *
     * @param messageType
     * @param messageBytes
     * @param parameterInfoList
     * @return
     */
    private static List getParameters(ChannelHandlerContext ctx, MessageType messageType, byte[] messageBytes, List<ParameterInfo> parameterInfoList) {
        List paramters = new ArrayList();

        for (ParameterInfo parameterInfo : parameterInfoList) {

            // TODO 这里需要重构, 支持多种消息类型, 同时也要支持 ChannelHandlerContext

            switch (messageType) {
                case PROTOBUF: {
                    if (parameterInfo.getParser() != null) {
                        addProtobugParam(messageBytes, paramters, parameterInfo);
                    }
                    break;

                }
                default:
                    throw new IllegalStateException("Unexpected value: " + messageType);
            }

        }
        return paramters;
    }

    private static void addProtobugParam(byte[] messageBytes, List paramters, ParameterInfo parameterInfo) {
        Parser parser = parameterInfo.getParser();
        try {
            Object result = parser.parseFrom(messageBytes);
            paramters.add(result);
        } catch (InvalidProtocolBufferException e) {
            log.error("解析失败", e);
        }
    }

    /**
     * 调用 #{@link CommandMapping} 注解的方法
     *
     * @param targetBeanClass
     * @param targetMethod
     * @param paramters
     * @return
     */
    private static Object invoke(Class targetBeanClass, Method targetMethod, List paramters) {
        Object result = null;
        try {
            Object methodBean = SpringContext.getBean(targetBeanClass);
            if (paramters.size() > 0) {
                Object[] params = paramters.toArray();
                result = targetMethod.invoke(methodBean, params);
            } else {
                result = targetMethod.invoke(methodBean);
            }
        } catch (IllegalAccessException e) {
            log.error("", e);
        } catch (InvocationTargetException e) {
            log.error("", e);
        } catch (BeansException e) {
            log.error("获取bean异常:{}", targetBeanClass.getSimpleName(), e);
        }
        return result;
    }

    /**
     * 对于 #{@link CommandMapping} 注解的方法产生的应答写回到前端去.
     * 此时需要区分写回的消息类型
     *
     * @param ctx
     * @param messageType
     * @param result
     */
    private static void response(ChannelHandlerContext ctx, MessageType messageType, Object result) {
        if (result == null) {
            return;
        }

        if (GeneratedMessageV3.class.isAssignableFrom(result.getClass())) {
            GeneratedMessageV3 generatedMessage = (GeneratedMessageV3) result;
            byte[] bytearray = generatedMessage.toByteArray();
            ByteBuf response = ByteBufAllocator.DEFAULT.heapBuffer(bytearray.length)
                    .writeByte(bytearray.length)
                    .writeByte(MessageType.PROTOBUF.getType())
                    .writeBytes(bytearray);
            ctx.writeAndFlush(response).addListener(listener -> {
                if (listener.isSuccess()) {
                    log.debug("消息发送成功");
                } else {
                    log.info("消息发送失败", listener.cause());
                }
            });
        }
    }


}
