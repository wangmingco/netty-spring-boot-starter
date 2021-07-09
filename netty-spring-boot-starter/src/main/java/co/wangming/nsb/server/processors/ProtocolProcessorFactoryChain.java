package co.wangming.nsb.server.processors;

import java.util.ArrayList;
import java.util.List;

public enum ProtocolProcessorFactoryChain {

    INSTANCE;

    private List<ProtocolProcessorFactory> factoryList = new ArrayList<>();

    public void addProtocolProcessorFactory(ProtocolProcessorFactory factory) {
        factoryList.add(factory);
    }

    public ProtocolProcessor getProtocolProcessor(Class type) {
        for (ProtocolProcessorFactory protocolProcessorFactory : factoryList) {
            ProtocolProcessor processor = protocolProcessorFactory.getProtocolProcessor(type);
            if (processor == null) {
                continue;
            }
            return processor;
        }

        return null;
    }
}
