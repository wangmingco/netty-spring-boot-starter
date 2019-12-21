package co.wangming.nsb.command;

import co.wangming.nsb.parsers.MessageParser;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created By WangMing On 2019-12-08
 **/
@Data
@Builder
public class CommandMethod {

    private List<MessageParser> messageParsers;

    private String beanName;

    @Override
    public String toString() {
        String parserNames = messageParsers.stream().map(it -> it.getClass().getSimpleName()).collect(Collectors.joining(","));
        return beanName + " [" + parserNames + "]";
    }
}
