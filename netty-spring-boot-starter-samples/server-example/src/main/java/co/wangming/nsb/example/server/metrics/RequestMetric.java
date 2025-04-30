package co.wangming.nsb.example.server.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author ming.wang
 * @date 2025/4/29
 */
public enum RequestMetric {

    DEFAULT();

    // 创建Meter来统计请求速率
    private final Meter requests;

    // 初始化报告器(这里使用SLF4J，你也可以用ConsoleReporter等)
    RequestMetric() {
        MetricRegistry metrics = new MetricRegistry();
        requests = metrics.meter("每秒钟请求速率");
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger("nsb.filter.metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);
    }

    public void requestMark() {
        requests.mark();
    }
}
