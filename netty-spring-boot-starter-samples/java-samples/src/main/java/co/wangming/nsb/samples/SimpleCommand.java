package co.wangming.nsb.samples;

import co.wangming.nsb.client.command.CommandSender;
import co.wangming.nsb.client.command.CommandTemplate;
import co.wangming.nsb.samples.protobuf.Search;
import co.wangming.nsb.server.command.CommandController;
import co.wangming.nsb.server.command.CommandMapping;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * Created By WangMing On 2019-12-07
 **/
@CommandController
public class SimpleCommand {

    private static final Logger log = LoggerFactory.getLogger(SimpleCommand.class);

    @Autowired
    private SimpleService simpleService;

    @CommandSender(host = "localhost", port = 7800)
    private CommandTemplate<GeneratedMessageV3> commandTemplate;

    @CommandMapping(requestId = 1)
    public Search.SearchResponse search(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest 1 --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());

        return Search.SearchResponse.newBuilder().setResult("查询成功").build();
    }

    @CommandMapping(requestId = 2)
    public Search.SearchResponse search2(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest 2 --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());

        simpleService.print();

        return Search.SearchResponse.newBuilder().setResult("查询成功").build();
    }

    @CommandMapping(requestId = 3)
    public void justSearch3(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest 3 --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());
    }

    @CommandMapping(requestId = 4)
    public void justSearch4(Search.SearchRequest searchRequest, String nullParam) {
        log.info("收到SearchRequest 4 --> {}, {}", searchRequest.getQuery(), nullParam);

        simpleService.print();
    }

    @CommandMapping(requestId = 5)
    public void justSearch5(Search.SearchRequest searchRequest, ChannelHandlerContext ctx) {
        log.info("收到SearchRequest 5 --> {}, {}", ctx.channel().remoteAddress(), searchRequest.getQuery());
    }

    @CommandMapping(requestId = 6)
    public void justSearch6(Search.SearchRequest searchRequest, ChannelHandlerContext ctx) {
        log.info("收到SearchRequest 6 --> {}, {}", ctx.channel().remoteAddress());
        throw new RuntimeException();
    }

    @CommandMapping(requestId = 7)
    public void justSearch7(Search.SearchRequest searchRequest, User user) {
        log.info("收到SearchRequest 7 --> {}", user.getChannelHandlerContext().channel().remoteAddress());
    }

    @CommandMapping(requestId = 8)
    public void justSearch8(Search.SearchRequest searchRequest, User user) {
        log.info("收到SearchRequest 8 --> {}", user.getChannelHandlerContext().channel().remoteAddress());

        commandTemplate.syncWrite(9, searchRequest);

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @CommandMapping(requestId = 9)
    public void justSearch9(Search.SearchRequest searchRequest, User user) {
        log.info("收到SearchRequest 9 --> {}", user.getChannelHandlerContext().channel().remoteAddress());

    }
}
