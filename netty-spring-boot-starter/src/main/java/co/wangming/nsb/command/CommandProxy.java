package co.wangming.nsb.command;

import co.wangming.nsb.parsers.MessageParser;

import java.util.List;

/**
 * Created By WangMing On 2019-12-11
 **/
public abstract class CommandProxy {

    private List<MessageParser> messageParsers;

    public List<MessageParser> getMessageParsers() {
        return messageParsers;
    }

    public void setMessageParsers(List<MessageParser> messageParsers) {
        this.messageParsers = messageParsers;
    }

    public abstract Object invoke(List paramters);
}
