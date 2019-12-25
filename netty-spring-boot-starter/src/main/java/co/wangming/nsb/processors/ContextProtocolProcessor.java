package co.wangming.nsb.processors;

import co.wangming.nsb.context.ContextCache;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@Slf4j
public class ContextProtocolProcessor implements MethodProtocolProcessor<byte[], Object> {

    @Override
    public void setParameterType(Class parameterType) {

    }

    @Override
    public Object serialize(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        ContextCache.get(ctx);

        return null;
    }

    @Override
    public byte[] deserialize(ChannelHandlerContext ctx, Object object) throws Exception {
        return null;
    }
}
