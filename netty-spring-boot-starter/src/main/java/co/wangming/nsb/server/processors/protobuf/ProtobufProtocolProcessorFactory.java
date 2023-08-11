package co.wangming.nsb.server.processors.protobuf;

import co.wangming.nsb.server.processors.ProtocolProcess;
import co.wangming.nsb.server.processors.ProtocolProcessor;
import co.wangming.nsb.server.processors.ProtocolProcessorFactory;
import com.google.protobuf.GeneratedMessageV3;

@ProtocolProcess(messageType = GeneratedMessageV3.class)
public class ProtobufProtocolProcessorFactory implements ProtocolProcessorFactory {

    @Override
    public ProtocolProcessor getProtocolProcessor(Class type) {
        if (!GeneratedMessageV3.class.isAssignableFrom(type)) {
            return null;
        }

        GeneratedMessageV3Processor processor = new GeneratedMessageV3Processor();
        processor.setParameterType(type);
        return processor;
    }

}
