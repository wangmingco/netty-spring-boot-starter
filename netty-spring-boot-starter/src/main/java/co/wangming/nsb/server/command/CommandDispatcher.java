package co.wangming.nsb.server.command;

import co.wangming.nsb.common.filter.FilterChain;
import co.wangming.nsb.common.filter.FilterContext;
import co.wangming.nsb.common.filter.FilterContextHolder;
import co.wangming.nsb.common.spring.SpringContext;
import co.wangming.nsb.server.processors.ProtocolProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By WangMing On 2019-12-07
 **/
public class CommandDispatcher {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

    public static ByteBuf dispatch(ChannelHandlerContext ctx, int messageId, ByteBuffer messageBytes) throws Exception {

        CommandProxy commandProxy = null;
        Class targetClass = null;
        Method targetMethod = null;
        List paramters = null;

        FilterContextHolder filterContextHolder = new FilterContextHolder();
        filterContextHolder.setFilterContext(FilterContext.continueFilter());

        try{
            String proxyBeanName = CommandProxy.class.getSimpleName() + "$$" + messageId;
            commandProxy = (CommandProxy) SpringContext.getBean(proxyBeanName);
            targetClass = commandProxy.getTargetClass();
            targetMethod = commandProxy.getTargetMethod();

            // 生成调用方法参数
            paramters = getParameters(ctx, messageBytes, commandProxy);
        } catch (Throwable throwable) {
            FilterChain.INSTANCE.onSystemException(filterContextHolder, targetClass, targetMethod, paramters, throwable);
            throw throwable;
        }

        FilterChain.INSTANCE.onBefore(filterContextHolder, targetClass, targetMethod, paramters);

        if (!filterContextHolder.getFilterContext().isInvokeContinue()) {
            return null;
        }

        Object result = null;
        try{
            // 调用方法
            result = invoke(commandProxy, paramters);
            FilterChain.INSTANCE.onAfter(filterContextHolder, targetClass, targetMethod, paramters, result);
            if (result == null) {
                return null;
            }
        } catch (Throwable throwable) {
            FilterChain.INSTANCE.onUserException(filterContextHolder, targetClass, targetMethod, paramters, throwable);
            throw throwable;
        }

        try{
            // 调用方法后可能产生应答, 将应答返回给前端
            return response(commandProxy, ctx, result);
        } catch (Throwable throwable) {
            FilterChain.INSTANCE.onSystemException(filterContextHolder, targetClass, targetMethod, paramters, throwable);
            throw throwable;
        }

    }

    /**
     * 生成调用  #{@link CommandMapping} 方法的参数.
     * 目前只支持Protobuf参数和 #{@link ChannelHandlerContext}
     *
     * @param messageBytes
     * @param commandProxy
     * @return
     */
    private static List getParameters(ChannelHandlerContext ctx, ByteBuffer messageBytes, CommandProxy commandProxy) throws Exception {

        List<ProtocolProcessor> protocolProcessors = commandProxy.getParameterProtocolProcessors();
        List paramters = new ArrayList();

        for (ProtocolProcessor protocolProcessor : protocolProcessors) {
            paramters.add(protocolProcessor.deserialize(ctx, messageBytes));
        }

        return paramters;
    }

    /**
     * 调用 #{@link CommandMapping} 注解的方法
     *
     * @param paramters
     * @return
     */
    public static Object invoke(CommandProxy commandProxy, List paramters) {
        return commandProxy.invoke(paramters);
    }

    /**
     * 对于 #{@link CommandMapping} 注解的方法产生的应答写回到前端去.
     * 此时需要区分写回的消息类型
     *
     * @param ctx
     * @param result
     * @return
     */
    private static ByteBuf response(CommandProxy commandProxy, ChannelHandlerContext ctx, Object result) throws Exception {
        if (result == null) {
            return null;
        }

        ProtocolProcessor parser = commandProxy.getReturnProtocolProcessor();
        byte[] bytearray = parser.serialize(ctx, result);

        /**
         * 使用池化的直接内存写入消息
         */
        return PooledByteBufAllocator.DEFAULT.directBuffer(bytearray.length)
                .writeByte(commandProxy.getResponseId())
                .writeByte(bytearray.length)
                .writeBytes(bytearray);
    }


}
