package co.wangming.nsb.server.processors;

import co.wangming.nsb.server.command.CommandDispatcher;
import co.wangming.nsb.server.context.ContextCache;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created By WangMing On 2019-12-20
 **/
public class ContextProtocolProcessor implements ProtocolProcessor<byte[], Object> {

    private static final Logger log = LoggerFactory.getLogger(CommandDispatcher.class);

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
