package co.wangming.nsb.example.server;

import co.wangming.nsb.example.server.metrics.EventMetric;
import co.wangming.nsb.server.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created By WangMing On 2019-12-20
 **/
@Event
public class SimpleEventHandler extends EventHandlerAdaptor<User> {

    private static final Logger log = LoggerFactory.getLogger(SimpleEventHandler.class);

    @Override
    public User fireChannelActiveEvent(ChannelActiveEvent channelActiveEvent) {
        EventMetric.DEFAULT.fireChannelActiveEvent();

        log.info("新的连接进来了:{}", channelActiveEvent.getChannelHandlerContext().name());
        User user = new User();
        user.setChannelHandlerContext(channelActiveEvent.getChannelHandlerContext());
        return user;
    }

    @Override
    public void fireChannelInactiveEvent(ChannelInactiveEvent<User> channelActiveEvent) {
        EventMetric.DEFAULT.fireChannelInactiveEvent();

        log.info("连接断开了:{}", channelActiveEvent.getContext());
    }

    @Override
    public void fireExceptionEvent(ExceptionEvent<User> exceptionEvent) {
        EventMetric.DEFAULT.fireExceptionEvent();

        log.error("发生异常:{}", exceptionEvent.getContext(), exceptionEvent.getCause());
    }

    @Override
    public void fireReaderIdleEvent(ReaderIdleEvent<User> readerIdleEvent) {
        EventMetric.DEFAULT.fireReaderIdleEvent();

        log.warn("连接读超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireWriterIdleEvent(WriterIdleEvent<User> readerIdleEvent) {
        EventMetric.DEFAULT.fireWriterIdleEvent();

        log.warn("连接写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireAllIdleEvent(AllIdleEvent<User> readerIdleEvent) {
        log.warn("连接读写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireUnknowEvent(UnknowEvent<User> unknowEvent) {
        EventMetric.DEFAULT.fireUnknowEvent();

        log.info("触发未知事件:{}", unknowEvent.getContext());
    }


}
