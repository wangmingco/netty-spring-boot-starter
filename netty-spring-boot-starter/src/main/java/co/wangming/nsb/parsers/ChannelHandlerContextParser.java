package co.wangming.nsb.parsers;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@ParserRegister(messageType = ChannelHandlerContext.class)
@Slf4j
public class ChannelHandlerContextParser implements MessageParser<byte[], ChannelHandlerContext> {

    @Override
    public void setParser(Class v) {

    }

    @Override
    public ChannelHandlerContext parse(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        return ctx;
    }
}
