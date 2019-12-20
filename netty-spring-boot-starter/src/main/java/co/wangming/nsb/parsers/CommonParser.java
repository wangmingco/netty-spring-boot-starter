package co.wangming.nsb.parsers;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@ParserRegister(messageType = Object.class)
@Slf4j
public class CommonParser implements MessageParser<byte[], Object> {

    @Override
    public void setParser(Class parameterType) {

    }

    @Override
    public Object parse(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        return null;
    }
}
