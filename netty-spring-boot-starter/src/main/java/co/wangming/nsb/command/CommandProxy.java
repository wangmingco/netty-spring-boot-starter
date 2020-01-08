package co.wangming.nsb.command;

import co.wangming.nsb.processors.ProtocolProcessor;
import lombok.Data;

import java.util.List;

/**
 * Created By WangMing On 2019-12-11
 **/
@Data
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
}
