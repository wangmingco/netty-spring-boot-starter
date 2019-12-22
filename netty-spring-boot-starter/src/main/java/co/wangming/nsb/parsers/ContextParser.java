package co.wangming.nsb.parsers;

import co.wangming.nsb.context.ContextCache;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@Slf4j
public class ContextParser implements MessageParser<byte[], Object> {

    @Override
    public void setParameterType(Class parameterType) {

    }

    @Override
    public Object parse(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        ContextCache.get(ctx);

        return null;
    }
}
