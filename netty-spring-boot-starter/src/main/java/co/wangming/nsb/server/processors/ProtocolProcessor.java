package co.wangming.nsb.server.processors;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created By WangMing On 2019-12-20
 **/
public interface ProtocolProcessor<T, U> {

    void setParameterType(Class parameterType);

    U deserialize(ChannelHandlerContext ctx, T t) throws Exception;

    byte[] serialize(ChannelHandlerContext ctx, U t) throws Exception;

}
