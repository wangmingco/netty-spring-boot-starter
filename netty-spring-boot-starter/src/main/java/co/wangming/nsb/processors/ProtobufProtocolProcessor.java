package co.wangming.nsb.processors;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;

/**
 * Created By WangMing On 2019-12-20
 **/
@ProtocolProcessorRegister(messageType = GeneratedMessageV3.class)
@Slf4j
public class ProtobufProtocolProcessor implements MethodProtocolProcessor<byte[], GeneratedMessageV3> {

    private Parser parser;

    @Override
    public void setParameterType(Class parameterType) {
        try {
            Field parserField = parameterType.getDeclaredField("PARSER");
            parserField.setAccessible(true);
            Parser parser = (Parser) parserField.get(parameterType);
            this.parser = parser;
        } catch (NoSuchFieldException e) {
            log.error("", e);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
    }

    @Override
    public GeneratedMessageV3 serialize(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        return (GeneratedMessageV3) parser.parseFrom(bytes);
    }

    @Override
    public byte[] deserialize(ChannelHandlerContext ctx, GeneratedMessageV3 bytes) throws Exception {
        return bytes.toByteArray();
    }
}
