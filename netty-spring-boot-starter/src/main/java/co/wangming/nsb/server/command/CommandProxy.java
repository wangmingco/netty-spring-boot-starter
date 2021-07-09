package co.wangming.nsb.server.command;

import co.wangming.nsb.server.processors.ProtocolProcessor;

import java.util.List;

/**
 * Created By WangMing On 2019-12-11
 **/
public abstract class CommandProxy {

    public static final String PARAMETER_PROCESSORS = "parameterProtocolProcessors";
    public static final String RETURN_PROCESSOR = "returnProtocolProcessor";
    public static final String REQUEST_ID = "requestId";
    public static final String RESPONSE_ID = "responseId";

    private int requestId;
    private int responseId;

    private List<ProtocolProcessor> parameterProtocolProcessors;

    private ProtocolProcessor returnProtocolProcessor;

    public abstract Object invoke(List paramters);

    public List<ProtocolProcessor> getParameterProtocolProcessors() {
        return parameterProtocolProcessors;
    }

    public ProtocolProcessor getReturnProtocolProcessor() {
        return returnProtocolProcessor;
    }

    public int getResponseId() {
        return responseId;
    }
}
