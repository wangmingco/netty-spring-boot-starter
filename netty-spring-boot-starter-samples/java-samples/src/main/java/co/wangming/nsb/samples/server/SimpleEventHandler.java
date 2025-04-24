package co.wangming.nsb.samples.server;

import co.wangming.nsb.samples.User;
import co.wangming.nsb.server.event.*;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created By WangMing On 2019-12-20
 **/
@Event
public class SimpleEventHandler extends EventHandlerAdaptor<User> {

    private static final Logger log = LoggerFactory.getLogger(SimpleEventHandler.class);

    // 创建Meter来统计请求速率
    private static final Meter fireChannelActiveEvent;
    private static final Meter fireChannelInactiveEvent;
    private static final Meter fireExceptionEvent;
    private static final Meter fireReaderIdleEvent;
    private static final Meter fireWriterIdleEvent;
    private static final Meter fireUnknowEvent;

    // 初始化报告器(这里使用SLF4J，你也可以用ConsoleReporter等)
    static {
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

    @Override
    public User fireChannelActiveEvent(ChannelActiveEvent channelActiveEvent) {
        fireChannelActiveEvent.mark();

        log.info("新的连接进来了:{}", channelActiveEvent.getChannelHandlerContext().name());
        User user = new User();
        user.setChannelHandlerContext(channelActiveEvent.getChannelHandlerContext());
        return user;
    }

    @Override
    public void fireChannelInactiveEvent(ChannelInactiveEvent<User> channelActiveEvent) {
        fireChannelInactiveEvent.mark();

        log.info("连接断开了:{}", channelActiveEvent.getContext());
    }

    @Override
    public void fireExceptionEvent(ExceptionEvent<User> exceptionEvent) {
        fireExceptionEvent.mark();

        log.error("发生异常:{}", exceptionEvent.getContext(), exceptionEvent.getCause());
    }

    @Override
    public void fireReaderIdleEvent(ReaderIdleEvent<User> readerIdleEvent) {
        fireReaderIdleEvent.mark();

        log.warn("连接读超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireWriterIdleEvent(WriterIdleEvent<User> readerIdleEvent) {
        fireWriterIdleEvent.mark();

        log.warn("连接写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireAllIdleEvent(AllIdleEvent<User> readerIdleEvent) {
        log.warn("连接读写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireUnknowEvent(UnknowEvent<User> unknowEvent) {
        fireUnknowEvent.mark();

        log.info("触发未知事件:{}", unknowEvent.getContext());
    }


}
