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
public enum EventMetric {

    DEFAULT();

    // 创建Meter来统计请求速率
    private final Meter fireChannelActiveEvent;
    private final Meter fireChannelInactiveEvent;
    private final Meter fireExceptionEvent;
    private final Meter fireReaderIdleEvent;
    private final Meter fireWriterIdleEvent;
    private final Meter fireUnknowEvent;

    // 初始化报告器(这里使用SLF4J，你也可以用ConsoleReporter等)
    EventMetric() {
        MetricRegistry metrics = new MetricRegistry();
        fireChannelActiveEvent = metrics.meter("fireChannelActiveEvent");
        fireChannelInactiveEvent = metrics.meter("fireChannelInactiveEvent");
        fireExceptionEvent = metrics.meter("fireExceptionEvent");
        fireReaderIdleEvent = metrics.meter("fireReaderIdleEvent");
        fireWriterIdleEvent = metrics.meter("fireWriterIdleEvent");
        fireUnknowEvent = metrics.meter("fireUnknowEvent");

        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger("nsb.event.metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);
    }

    public void fireChannelActiveEvent() {
        fireChannelActiveEvent.mark();
    }

    public void fireChannelInactiveEvent() {
        fireChannelInactiveEvent.mark();
    }

    public void fireExceptionEvent() {
        fireExceptionEvent.mark();
    }

    public void fireReaderIdleEvent() {
        fireReaderIdleEvent.mark();
    }

    public void fireWriterIdleEvent() {
        fireWriterIdleEvent.mark();
    }

    public void fireUnknowEvent() {
        fireUnknowEvent.mark();
    }

}
