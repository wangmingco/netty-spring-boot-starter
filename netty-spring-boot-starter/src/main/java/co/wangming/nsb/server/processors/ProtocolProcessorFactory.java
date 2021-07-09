package co.wangming.nsb.server.processors;

public interface ProtocolProcessorFactory {

    public ProtocolProcessor getProtocolProcessor(Class type);

}
