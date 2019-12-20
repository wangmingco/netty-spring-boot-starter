package co.wangming.nsb.event;

import co.wangming.nsb.cache.ContextCache;
import co.wangming.nsb.springboot.SpringContext;
import io.netty.channel.Channel;
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
            Object context = eventHandler.channelActive(channelActiveEvent);

            ContextCache.put(getChannelId(ctx), context);
        }
    }

    public static void dispatchChannelInactiveEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;
            ChannelInactiveEvent channelActiveEvent = new ChannelInactiveEvent();
            channelActiveEvent.setChannelHandlerContext(ctx);
            channelActiveEvent.setContext(getContext(ctx));
            eventHandler.channelInactive(channelActiveEvent);
        }
    }

    public static void dispatchExceptionEvent(ChannelHandlerContext ctx, Throwable cause) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ExceptionEvent exceptionEvent = new ExceptionEvent();
            exceptionEvent.setChannelHandlerContext(ctx);
            exceptionEvent.setContext(getContext(ctx));
            exceptionEvent.setCause(cause);
            eventHandler.exceptionEvent(exceptionEvent);
        }
    }

    public static void dispatchReaderIdleEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            ReaderIdleEvent readerIdleEvent = new ReaderIdleEvent();
            readerIdleEvent.setChannelHandlerContext(ctx);
            readerIdleEvent.setContext(getContext(ctx));
            eventHandler.readerIdleEvent(readerIdleEvent);
        }
    }

    public static void dispatchWriterIdleEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            WriterIdleEvent writerIdleEvent = new WriterIdleEvent();
            writerIdleEvent.setChannelHandlerContext(ctx);
            writerIdleEvent.setContext(getContext(ctx));
            eventHandler.writerIdleEvent(writerIdleEvent);
        }
    }

    public static void dispatchAllIdleEvent(ChannelHandlerContext ctx) {
        Map<String, Object> eventHandlers = SpringContext.getBeansWithAnnotation(EventRegister.class);
        for (Object value : eventHandlers.values()) {
            EventHandler eventHandler = (EventHandler) value;

            AllIdleEvent allIdleEvent = new AllIdleEvent();
            allIdleEvent.setChannelHandlerContext(ctx);
            allIdleEvent.setContext(getContext(ctx));
            eventHandler.allIdleEvent(allIdleEvent);
        }
    }

    private static Object getContext(ChannelHandlerContext ctx) {
        return ContextCache.get(getChannelId(ctx));
    }

    private static String getChannelId(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        return channel.remoteAddress() + "_" + channel.localAddress();
    }
}
