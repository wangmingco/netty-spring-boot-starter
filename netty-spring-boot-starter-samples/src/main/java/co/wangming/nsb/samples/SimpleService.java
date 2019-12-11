package co.wangming.nsb.samples;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created By WangMing On 2019-12-11
 **/
@Component
@Slf4j
public class SimpleService {

    public void print() {
        log.info("helloworld");
    }
}
