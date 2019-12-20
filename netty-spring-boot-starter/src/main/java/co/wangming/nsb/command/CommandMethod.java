package co.wangming.nsb.command;

import co.wangming.nsb.parsers.MessageParser;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Created By WangMing On 2019-12-08
 **/
@Data
@Builder
public class CommandMethod {

    private List<MessageParser> messageParsers;

    private String beanName;
}
