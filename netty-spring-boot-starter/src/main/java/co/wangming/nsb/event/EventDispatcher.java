package co.wangming.nsb.event;

import co.wangming.nsb.springboot.SpringContext;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;

/**
 * Created By WangMing On 2019-12-20
 **/
public class EventDispatcher {

    public static void dispatchChannelActiveEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ChannelActiveEvent channelActiveEvent = new ChannelActiveEvent();
            channelActiveEvent.setChannelHandlerContext(ctx);
            eventHandler.channelActive(channelActiveEvent);
        }
    }

    public static void dispatchChannelInactiveEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;
            ChannelInactiveEvent channelActiveEvent = new ChannelInactiveEvent();
            channelActiveEvent.setChannelHandlerContext(ctx);
            eventHandler.channelInactive(channelActiveEvent);
        }
    }

    public static void dispatchExceptionEvent(ChannelHandlerContext ctx, Throwable cause) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ExceptionEvent exceptionEvent = new ExceptionEvent();
            exceptionEvent.setChannelHandlerContext(ctx);
            exceptionEvent.setCause(cause);
            eventHandler.exception(exceptionEvent);
        }
    }

    public static void dispatchReaderIdleEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ReaderIdleEvent readerIdleEvent = new ReaderIdleEvent();
            readerIdleEvent.setChannelHandlerContext(ctx);
            eventHandler.readerIdle(readerIdleEvent);
        }
    }

    public static void dispatchWriterIdleEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            WriterIdleEvent writerIdleEvent = new WriterIdleEvent();
            writerIdleEvent.setChannelHandlerContext(ctx);
            eventHandler.writerIdle(writerIdleEvent);
        }
    }

    public static void dispatchAllIdleEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            AllIdleEvent allIdleEvent = new AllIdleEvent();
            allIdleEvent.setChannelHandlerContext(ctx);
            eventHandler.allIdle(allIdleEvent);
        }
    }

}
