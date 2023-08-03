package co.wangming.nsb.server.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public enum ProtocolProcessorFactoryChain {

    INSTANCE;

    private static final Logger log = LoggerFactory.getLogger(ProtocolProcessorFactoryChain.class);

    private Map<Class, ProtocolProcessorFactory> factoryMap = new HashMap<>();

    public void addProtocolProcessorFactory(ProtocolProcessorFactory factory) {
        Class messageType = factory.getClass().getAnnotation(NSProtocolProcessor.class).messageType();
        factoryMap.put(messageType, factory);
    }

    public ProtocolProcessor getProtocolProcessor(Class type) {
        for (Map.Entry<Class, ProtocolProcessorFactory> entry : factoryMap.entrySet()) {
            Class aClass = entry.getKey();
            ProtocolProcessorFactory factory = entry.getValue();
            if (aClass.isAssignableFrom(type)) {
                return factory.getProtocolProcessor(type);
            }
        }
        return null;
    }
}
