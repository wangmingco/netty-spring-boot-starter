package co.wangming.nsb.netty;

import co.wangming.nsb.parameterHandlers.ParameterHandler;
import co.wangming.nsb.parameterHandlers.ParameterInfo;
import co.wangming.nsb.springboot.SpringContext;
import co.wangming.nsb.util.CommandMethodCache;
import co.wangming.nsb.vo.MethodInfo;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created By WangMing On 2019-12-07
 **/
@Slf4j
public class CommandDispatcher {

    public static void dispatch(ChannelHandlerContext ctx, int messageId, byte[] messageBytes) {
        MethodInfo methodInfo = CommandMethodCache.getMethodInfo(String.valueOf(messageId));
        List<ParameterInfo> parameterInfoList = methodInfo.getParameterInfoList();
        String beanName = methodInfo.getBeanName();

        // 生成调用方法参数
        List paramters = getParameters(ctx, messageBytes, parameterInfoList);

        // 调用方法
        Object result = invoke(beanName, messageId, paramters);

        // 调用方法后可能产生应答, 将应答返回给前端
        response(ctx, result);
    }

    /**
     * 生成调用  #{@link CommandMapping} 方法的参数.
     * 目前只支持Protobuf参数和 #{@link ChannelHandlerContext}
     *
     * @param messageBytes
     * @param parameterInfoList
     * @return
     */
    private static List getParameters(ChannelHandlerContext ctx, byte[] messageBytes, List<ParameterInfo> parameterInfoList) {
        List paramters = new ArrayList();

        Map<String, ParameterHandler> handlers = SpringContext.getBeansOfType(ParameterHandler.class);

        for (ParameterInfo parameterInfo : parameterInfoList) {

            if (GeneratedMessageV3.class.isAssignableFrom(parameterInfo.getParameterType())) {
                paramters.add(addProtobugParam(messageBytes, paramters, parameterInfo));
            } else if (ChannelHandlerContext.class.isAssignableFrom(parameterInfo.getParameterType())) {
                paramters.add(ctx);
            } else {
                paramters.add(null);
            }
        }

        return paramters;
    }

    private static Object addProtobugParam(byte[] messageBytes, List paramters, ParameterInfo parameterInfo) {
        Parser parser = parameterInfo.getParser();
        try {
            Object result = parser.parseFrom(messageBytes);

            return result;
        } catch (InvalidProtocolBufferException e) {
            log.error("解析失败", e);
        }
        return null;
    }

    /**
     * 调用 #{@link CommandMapping} 注解的方法
     *
     * @param paramters
     * @return
     */
    private static Object invoke(String beanName, int messageId, List paramters) {
        String proxyBeanName = beanName + "$$" + CommandProxy.class.getSimpleName() + "$$" + messageId;
        Object result = null;
        try {
            CommandProxy methodBean = (CommandProxy) SpringContext.getBean(proxyBeanName);
            return methodBean.invoke(paramters);
        } catch (BeansException e) {
            log.error("获取bean异常:{}", proxyBeanName, e);
        }
        return result;
    }

    /**
     * 对于 #{@link CommandMapping} 注解的方法产生的应答写回到前端去.
     * 此时需要区分写回的消息类型
     *
     * @param ctx
     * @param result
     */
    private static void response(ChannelHandlerContext ctx, Object result) {
        if (result == null) {
            return;
        }

        if (GeneratedMessageV3.class.isAssignableFrom(result.getClass())) {
            GeneratedMessageV3 generatedMessage = (GeneratedMessageV3) result;
            byte[] bytearray = generatedMessage.toByteArray();
            ByteBuf response = ByteBufAllocator.DEFAULT.heapBuffer(bytearray.length)
                    .writeByte(bytearray.length)
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
