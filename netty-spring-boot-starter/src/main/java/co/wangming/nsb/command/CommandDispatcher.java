package co.wangming.nsb.command;

import co.wangming.nsb.parsers.MessageParser;
import co.wangming.nsb.springboot.SpringContext;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By WangMing On 2019-12-07
 **/
@Slf4j
public class CommandDispatcher {

    public static void dispatch(ChannelHandlerContext ctx, int messageId, byte[] messageBytes) throws Exception {

        String proxyBeanName = CommandProxy.class.getSimpleName() + "$$" + messageId;
        CommandProxy commandProxy = (CommandProxy) SpringContext.getBean(proxyBeanName);

        // 生成调用方法参数
        List paramters = getParameters(ctx, messageBytes, commandProxy);

        // 调用方法
        Object result = invoke(commandProxy, paramters);

        // 调用方法后可能产生应答, 将应答返回给前端
        response(ctx, result);
    }

    /**
     * 生成调用  #{@link CommandMapping} 方法的参数.
     * 目前只支持Protobuf参数和 #{@link ChannelHandlerContext}
     *
     * @param messageBytes
     * @param commandProxy
     * @return
     */
    private static List getParameters(ChannelHandlerContext ctx, byte[] messageBytes, CommandProxy commandProxy) throws Exception {

        List<MessageParser> messageParsers = commandProxy.getMessageParsers();
        List paramters = new ArrayList();

        for (co.wangming.nsb.parsers.MessageParser messageParser : messageParsers) {
            paramters.add(messageParser.parse(ctx, messageBytes));
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
