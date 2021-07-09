package co.wangming.nsb.server.processors.protobuf;

import co.wangming.nsb.server.processors.ProtocolProcessor;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Parser;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Created By WangMing On 2019-12-20
 **/
public class GeneratedMessageV3Processor implements ProtocolProcessor<byte[], GeneratedMessageV3> {

    private static final Logger log = LoggerFactory.getLogger(GeneratedMessageV3Processor.class);

    private Parser parser;

    @Override
    public void setParameterType(Class parameterType) {
        try {
            Field parserField = parameterType.getDeclaredField("PARSER");
            parserField.setAccessible(true);
            Parser parser = (Parser) parserField.get(parameterType);
            parserField.setAccessible(false);
            this.parser = parser;
        } catch (NoSuchFieldException e) {
            log.error("", e);
        } catch (IllegalAccessException e) {
            log.error("", e);
        }
    }

    @Override
    public GeneratedMessageV3 deserialize(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        return (GeneratedMessageV3) parser.parseFrom(bytes);
    }

    @Override
    public byte[] serialize(ChannelHandlerContext ctx, GeneratedMessageV3 bytes) throws Exception {
        return bytes.toByteArray();
    }
}
