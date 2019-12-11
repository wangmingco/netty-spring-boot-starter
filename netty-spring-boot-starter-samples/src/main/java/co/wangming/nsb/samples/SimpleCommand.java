package co.wangming.nsb.samples;

import co.wangming.nsb.netty.CommandController;
import co.wangming.nsb.netty.CommandMapping;
import co.wangming.nsb.samples.protobuf.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created By WangMing On 2019-12-07
 **/
@Slf4j
@CommandController
public class SimpleCommand {

    @Autowired
    private SimpleService simpleService;

    @CommandMapping(id = 1)
    public Search.SearchResponse search(Search.SearchRequest searchRequest) {
        log.info("收到SearchRequest --> {}, {}, {}", searchRequest.getQuery(), searchRequest.getPageNumber(), searchRequest.getResultPerPage());

        simpleService.print();

        return Search.SearchResponse.newBuilder().setResult("查询成功").build();
    }

}
