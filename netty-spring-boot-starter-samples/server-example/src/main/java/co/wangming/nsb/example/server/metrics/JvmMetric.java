package co.wangming.nsb.example.server.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.jvm.*;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author ming.wang
 * @date 2025/4/29
 */
@Component
public class JvmMetric implements CommandLineRunner {


    public static final MetricRegistry metrics = new MetricRegistry();

    @Override
    public void run(String... args) throws Exception {
        gcMetric();
        classLoadingMetric();
        memoryUsageMetric();
        threadStatesUsageMetric();
    }

    private void gcMetric() {
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new GarbageCollectorMetricSet());

        startReporter(registry, "jvm.gc.metrics");
    }

    private void classLoadingMetric() {
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new ClassLoadingGaugeSet());

        startReporter(registry, "jvm.classLoading.metrics");
    }

    private void memoryUsageMetric() {
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new MemoryUsageGaugeSet());

        startReporter(registry, "jvm.memoryUsage.metrics");
    }

    private void threadStatesUsageMetric() {
        MetricRegistry registry = new MetricRegistry();
        registry.registerAll(new ThreadStatesGaugeSet());

        startReporter(registry, "jvm.threadStates.metrics");
    }

    private void startReporter(MetricRegistry metrics, String logger) {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger(logger))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);
    }

}
