package co.wangming.nsb.command;

import co.wangming.nsb.processors.MethodProtocolProcessor;

import java.util.List;

/**
 * Created By WangMing On 2019-12-11
 **/
public abstract class CommandProxy {

    public static final String PARAMETER_PROCESSORS = "parameterProtocolProcessors";
    public static final String RETURN_PROCESSOR = "returnProtocolProcessor";

    private List<MethodProtocolProcessor> parameterProtocolProcessors;

    private MethodProtocolProcessor returnProtocolProcessor;

    public List<MethodProtocolProcessor> getParameterProtocolProcessors() {
        return parameterProtocolProcessors;
    }

    public void setParameterProtocolProcessors(List<MethodProtocolProcessor> parameterProtocolProcessors) {
        this.parameterProtocolProcessors = parameterProtocolProcessors;
    }

    public MethodProtocolProcessor getReturnProtocolProcessor() {
        return returnProtocolProcessor;
    }

    public void setReturnProtocolProcessor(MethodProtocolProcessor returnProtocolProcessor) {
        this.returnProtocolProcessor = returnProtocolProcessor;
    }

    public abstract Object invoke(List paramters);
}
