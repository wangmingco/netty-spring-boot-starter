package co.wangming.nsb.client.command;

import co.wangming.nsb.client.netty.ChannelProxy;
import co.wangming.nsb.common.SpringContext;
import co.wangming.nsb.server.processors.ProtocolProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created By WangMing On 2020-01-02
 **/
public class CommandTemplate<T> {

    private static final Logger log = LoggerFactory.getLogger(CommandTemplate.class);

    private ChannelProxy channelProxy;

    private Class tClass;

    public CommandTemplate() {
    }

    public void syncWrite(Integer messageId, T msg) {
        if (!channelProxy.isConnected()) {
            channelProxy.connect();
        }
        if (tClass == null) {
            return;
        }
        // TODO 抽象出能够自动识别多种协议
        String protocolProcessorName = tClass.getSimpleName() + "ProtocolProcessor";
        ProtocolProcessor<Void, T> protocolProcessor = (ProtocolProcessor) SpringContext.getBean(protocolProcessorName);
        byte[] bytearray = null;
        try {
            bytearray = protocolProcessor.serialize(null, msg);
        } catch (Exception e) {
        }

        channelProxy.syncWrite(messageId, bytearray);
    }

    public Class gettClass() {
        return tClass;
    }

    public void settClass(Class tClass) {
        this.tClass = tClass;
    }


    public ChannelProxy<T> getChannelProxy() {
        return channelProxy;
    }

    public void connect(String host, Integer port) {
        this.channelProxy = new ChannelProxy(host, port);
        this.channelProxy.connect();
    }
}
