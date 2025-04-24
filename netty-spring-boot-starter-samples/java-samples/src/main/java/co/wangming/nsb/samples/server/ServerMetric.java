package co.wangming.nsb.samples.server;

import co.wangming.nsb.common.springboot.SpringBootNettyTCPProperties;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import io.netty.buffer.*;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Spliterators;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

/**
 *
 * @author ming.wang
 * @date 2025/4/23
 */
@Component
public class ServerMetric implements CommandLineRunner {

    @Resource
    private SpringBootNettyTCPProperties springBootNettyTCPProperties;

    public static final MetricRegistry metrics = new MetricRegistry();

    @Override
    public void run(String... args) throws Exception {
        springBootNettyTCPProperties.setAllocator(PooledByteBufAllocator.class.getName());
        PooledByteBufAllocator allocator = (PooledByteBufAllocator)springBootNettyTCPProperties.getAllocator();
        setupMetrics(allocator);
        startReporter();
    }

    private void setupMetrics(PooledByteBufAllocator allocator) {

        // 监控Netty内存池使用情况
        PooledByteBufAllocatorMetric metric = allocator.metric();

        // 直接内存指标
        metrics.register(MetricRegistry.name(ServerMetric.class, "direct", "used"),
                (Gauge<Long>) metric::usedDirectMemory);

        metrics.register(MetricRegistry.name(ServerMetric.class, "direct", "max"),
                (Gauge<Integer>) metric::numDirectArenas);

        // 堆内存指标
        metrics.register(MetricRegistry.name(ServerMetric.class, "heap", "used"),
                (Gauge<Long>) metric::usedHeapMemory);

        metrics.register(MetricRegistry.name(ServerMetric.class, "heap", "max"),
                (Gauge<Integer>) metric::numHeapArenas);

        // 更详细的内存池指标
        registerArenaMetrics("direct", metric.directArenas());
        registerArenaMetrics("heap", metric.heapArenas());
    }

    private void registerArenaMetrics(String type, List<PoolArenaMetric> arenas) {
        for (int i = 0; i < arenas.size(); i++) {
            PoolArenaMetric arena = arenas.get(i);
            String prefix = MetricRegistry.name(ServerMetric.class, type, "arena" + i);

            // 注册各种内存池指标
            metrics.register(prefix + ".numThreadCaches",
                    (Gauge<Integer>) arena::numThreadCaches);
            metrics.register(prefix + ".numTinySubpages",
                    (Gauge<Integer>) arena::numTinySubpages);
            metrics.register(prefix + ".numSmallSubpages",
                    (Gauge<Integer>) arena::numSmallSubpages);
            metrics.register(prefix + ".numChunkLists",
                    (Gauge<Integer>) arena::numChunkLists);

            // 注册每个chunk list的指标
            registerChunkListMetrics(prefix, arena.chunkLists());
        }
    }

    private void registerChunkListMetrics(String prefix, List<PoolChunkListMetric> chunkLists) {
        for (int i = 0; i < chunkLists.size(); i++) {
            PoolChunkListMetric chunkList = chunkLists.get(i);
            String chunkListPrefix = prefix + ".chunkList" + i;

            long numChunks = StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(chunkList.iterator(), 0),
                    false
            ).count();

            metrics.register(chunkListPrefix + ".minUsage",
                    (Gauge<Integer>) chunkList::minUsage);
            metrics.register(chunkListPrefix + ".maxUsage",
                    (Gauge<Integer>) chunkList::maxUsage);
            metrics.register(chunkListPrefix + ".numChunks",
                    (Gauge<Long>) () -> numChunks);
        }
    }

    private void startReporter() {
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metrics)
                .outputTo(LoggerFactory.getLogger("nsb.netty.metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.MINUTES);
    }


}
