package co.wangming.nsb.samples;

import co.wangming.nsb.event.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@EventRegister
@Slf4j
public class SimpleEventHandler extends EventHandlerAdaptor<User> {

    @Override
    public User fireChannelActiveEvent(ChannelActiveEvent channelActiveEvent) {

        log.info("新的连接进来了:{}", channelActiveEvent.getChannelHandlerContext().name());
        User user = new User();
        user.setChannelHandlerContext(channelActiveEvent.getChannelHandlerContext());
        return user;
    }

    @Override
    public void fireChannelInactiveEvent(ChannelInactiveEvent<User> channelActiveEvent) {
        log.info("连接断开了:{}", channelActiveEvent.getContext());
    }

    @Override
    public void fireExceptionEvent(ExceptionEvent<User> exceptionEvent) {
        log.info("发生异常:{}", exceptionEvent.getContext(), exceptionEvent.getCause());
    }

    @Override
    public void fireReaderIdleEvent(ReaderIdleEvent<User> readerIdleEvent) {
        log.info("连接读超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireWriterIdleEvent(WriterIdleEvent<User> readerIdleEvent) {
        log.info("连接写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireAllIdleEvent(AllIdleEvent<User> readerIdleEvent) {
        log.info("连接读写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void fireUnknowEvent(UnknowEvent<User> unknowEvent) {
        log.info("触发未知事件:{}", unknowEvent.getContext());
    }


}
