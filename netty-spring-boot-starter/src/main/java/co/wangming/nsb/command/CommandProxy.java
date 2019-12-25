package co.wangming.nsb.command;

import co.wangming.nsb.parsers.MessageParser;

import java.util.List;

/**
 * Created By WangMing On 2019-12-11
 **/
public abstract class CommandProxy {

    public static final String PARAMETER_PARSERS = "parameterParsers";

    private List<MessageParser> parameterParsers;

    public List<MessageParser> getParameterParsers() {
        return parameterParsers;
    }

    public void setParameterParsers(List<MessageParser> parameterParsers) {
        this.parameterParsers = parameterParsers;
    }

    public abstract Object invoke(List paramters);
}
