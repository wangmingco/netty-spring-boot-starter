package co.wangming.nsb.processors;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created By WangMing On 2019-12-20
 **/
public interface MethodProtocolProcessor<T, U> {

    void setParameterType(Class parameterType);

    U serialize(ChannelHandlerContext ctx, T t) throws Exception;

    byte[] deserialize(ChannelHandlerContext ctx, U t) throws Exception;

}
