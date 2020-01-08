package co.wangming.nsb.processors;

import co.wangming.nsb.context.ContextCache;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@Slf4j
public class ContextProtocolProcessor implements ProtocolProcessor<byte[], Object> {

    @Override
    public void setParameterType(Class parameterType) {

    }

    @Override
    public Object deserialize(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        ContextCache.get(ctx);

        return null;
    }

    @Override
    public byte[] serialize(ChannelHandlerContext ctx, Object object) throws Exception {
        return null;
    }
}
