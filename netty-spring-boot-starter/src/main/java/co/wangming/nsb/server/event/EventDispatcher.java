package co.wangming.nsb.server.event;

import co.wangming.nsb.common.spring.SpringContext;
import co.wangming.nsb.server.spring.NSEventRegistrar;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created By WangMing On 2019-12-20
 **/
public class EventDispatcher {

    private static final Logger log = LoggerFactory.getLogger(NSEventRegistrar.class);

    public static void dispatchChannelActiveEvent(ChannelHandlerContext ctx) {
        log.info("处理Channel激活事件");

        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ChannelActiveEvent channelActiveEvent = new ChannelActiveEvent();
            channelActiveEvent.setChannelHandlerContext(ctx);
            eventHandler.channelActive(channelActiveEvent);
        }
    }

    public static void dispatchChannelInactiveEvent(ChannelHandlerContext ctx) {
        log.info("处理Channel失活事件");

        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;
            ChannelInactiveEvent channelActiveEvent = new ChannelInactiveEvent();
            channelActiveEvent.setChannelHandlerContext(ctx);
            eventHandler.channelInactive(channelActiveEvent);
        }
    }

    public static void dispatchExceptionEvent(ChannelHandlerContext ctx, Throwable cause) {
        log.info("处理Channel异常事件");
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ExceptionEvent exceptionEvent = new ExceptionEvent();
            exceptionEvent.setChannelHandlerContext(ctx);
            exceptionEvent.setCause(cause);
            eventHandler.exception(exceptionEvent);
        }
    }

    public static void dispatchReaderIdleEvent(ChannelHandlerContext ctx) {
        log.info("处理Channel读空闲事件");

        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ReaderIdleEvent readerIdleEvent = new ReaderIdleEvent();
            readerIdleEvent.setChannelHandlerContext(ctx);
            eventHandler.readerIdle(readerIdleEvent);
        }
    }

    public static void dispatchWriterIdleEvent(ChannelHandlerContext ctx) {
        log.info("处理Channel写空闲事件");

        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            WriterIdleEvent writerIdleEvent = new WriterIdleEvent();
            writerIdleEvent.setChannelHandlerContext(ctx);
            eventHandler.writerIdle(writerIdleEvent);
        }
    }

    public static void dispatchAllIdleEvent(ChannelHandlerContext ctx) {
        log.info("处理Channel读写双空闲事件");

        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            AllIdleEvent allIdleEvent = new AllIdleEvent();
            allIdleEvent.setChannelHandlerContext(ctx);
            eventHandler.allIdle(allIdleEvent);
        }
    }

    public static void dispatchUnknowEvent(ChannelHandlerContext ctx) {
        log.info("处理Channel读写未知事件");

        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(NSEvent.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            UnknowEvent unknowEvent = new UnknowEvent();
            unknowEvent.setChannelHandlerContext(ctx);
            eventHandler.unknow(unknowEvent);
        }
    }

}
