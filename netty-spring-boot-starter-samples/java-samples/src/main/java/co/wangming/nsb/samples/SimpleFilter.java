package co.wangming.nsb.samples;

import co.wangming.nsb.common.filter.EmptyFilter;
import co.wangming.nsb.common.filter.Filter;
import co.wangming.nsb.common.filter.FilterContextHolder;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author ming.wang
 * @date 2023/8/11
 */
@Filter
public class SimpleFilter extends EmptyFilter {

    private static final Logger log = LoggerFactory.getLogger(SimpleFilter.class);

    // 创建Metric注册表
    private static final MetricRegistry metrics = new MetricRegistry();

    // 创建Meter来统计请求速率
    private static final Meter requests = metrics.meter("每秒钟请求速率");

    // 初始化报告器(这里使用SLF4J，你也可以用ConsoleReporter等)
    static {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger("nsb.filter.metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(10, TimeUnit.SECONDS); // 每分钟报告一次
    }

    @Override
    public void onBefore(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters) {

        requests.mark();

        log.info("开始调用 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }

    @Override
    public void onAfter(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Object result) {
        log.info("调用完成 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }

    @Override
    public void onSystemException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
        log.error("调用异常 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }

    @Override
    public void onUserException(FilterContextHolder filterContext, Class targetClass, Method targetMethod, List parameters, Throwable throwable) {
        log.error("调用异常 {}#{}", targetClass.getSimpleName(), targetMethod.getName());
    }
}
