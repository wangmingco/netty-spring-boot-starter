package co.wangming.nsb.samples;

import co.wangming.nsb.event.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Created By WangMing On 2019-12-20
 **/
@EventRegister
@Slf4j
public class SimpleEventHandler implements EventHandler<String> {
    @Override
    public String channelActive(ChannelActiveEvent channelActiveEvent) {

        log.info("新的连接进来了:{}", channelActiveEvent.getChannelHandlerContext().name());
        return channelActiveEvent.getChannelHandlerContext().name();
    }

    @Override
    public void channelInactive(ChannelInactiveEvent<String> channelActiveEvent) {
        log.info("连接断开了:{}", channelActiveEvent.getContext());
    }

    @Override
    public void exceptionEvent(ExceptionEvent<String> exceptionEvent) {
        log.info("发生异常:{}", exceptionEvent.getContext(), exceptionEvent.getCause());
    }

    @Override
    public void readerIdleEvent(ReaderIdleEvent<String> readerIdleEvent) {
        log.info("连接读超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void writerIdleEvent(WriterIdleEvent<String> readerIdleEvent) {
        log.info("连接写超时:{}", readerIdleEvent.getContext());
    }

    @Override
    public void allIdleEvent(AllIdleEvent<String> readerIdleEvent) {
        log.info("连接读写超时:{}", readerIdleEvent.getContext());
    }
}
