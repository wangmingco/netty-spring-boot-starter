package co.wangming.nsb.server.spring;

import co.wangming.nsb.server.event.EventRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于扫描 #{@link EventRegister} 注解
 * <p>
 * Created By WangMing On 2019-12-06
 **/
public class CommonScannerRegistrar extends AbstractCommandScannerRegistrar {

    private static final Logger log = LoggerFactory.getLogger(CommonScannerRegistrar.class);

    private static List<Class> classes = new ArrayList() {{
        add(EventRegister.class);
    }};

    public List<Class> getAnnotationTypeFilterClass() {
        return classes;
    }

}
