package co.wangming.nsb.client.command;

import co.wangming.nsb.client.netty.ChannelProxy;
import co.wangming.nsb.common.spring.SpringContext;
import co.wangming.nsb.server.processors.ProtocolProcessor;
import co.wangming.nsb.server.processors.ProtocolProcessorFactoryChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created By WangMing On 2020-01-02
 **/
public class CommandTemplate<T> {

    private static final Logger log = LoggerFactory.getLogger(CommandTemplate.class);

    private ChannelProxy channelProxy;

    private ProtocolProcessor<Void, T> protocolProcessor;

    public CommandTemplate() {
    }

    public void syncWrite(Integer messageId, T msg) throws Exception {
        try{
            if (!channelProxy.isConnected()) {
                channelProxy.connect();
            }
            if (protocolProcessor == null) {
                return;
            }

            byte[] bytearray = protocolProcessor.serialize(null, msg);
            channelProxy.syncWrite(messageId, bytearray);
        } catch (Exception e) {
            throw e;
        }

    }

    public void settClass(Class tClass) {
        try {
            protocolProcessor = ProtocolProcessorFactoryChain.INSTANCE.getProtocolProcessor(tClass);
        } catch (Exception e) {
            log.error("从Spring中找不到发送端的Processor: {}", tClass.getSimpleName(), e);
        }
    }

    public void connect(String protocol, String host, Integer port) throws InterruptedException {
        this.channelProxy = new ChannelProxy(protocol, host, port);
        this.channelProxy.connect();
    }
}
