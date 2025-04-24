package co.wangming.nsb.samples.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created By WangMing On 2019-12-11
 **/
@Component
public class SimpleService {

    private static final Logger log = LoggerFactory.getLogger(SimpleService.class);

    public void print() {
        log.info("helloworld");
    }
}
