package co.wangming.nsb.event;

import co.wangming.nsb.context.ContextCache;
import co.wangming.nsb.context.ContextWrapper;

/**
 * Created By WangMing On 2019-12-22
 **/
public abstract class EventHandlerAdaptor<T> implements EventHandler {

    @Override
    public void channelActive(ChannelActiveEvent channelActiveEvent) {
        Object context = fireChannelActiveEvent(channelActiveEvent);

        ContextCache.put(channelActiveEvent.getChannelHandlerContext(), context);
    }

    public abstract Object fireChannelActiveEvent(ChannelActiveEvent channelActiveEvent);

    @Override
    public void channelInactive(ChannelInactiveEvent channelInactiveEvent) {
        channelInactiveEvent.setContext(getContext(channelInactiveEvent));

        fireChannelInactiveEvent(channelInactiveEvent);

        ContextCache.remove(channelInactiveEvent.getChannelHandlerContext());
    }

    public abstract void fireChannelInactiveEvent(ChannelInactiveEvent<T> channelInactiveEvent);

    @Override
    public void exception(ExceptionEvent exceptionEvent) {

        exceptionEvent.setContext(getContext(exceptionEvent));

        fireExceptionEvent(exceptionEvent);
    }

    public abstract void fireExceptionEvent(ExceptionEvent<T> exceptionEvent);

    @Override
    public void readerIdle(ReaderIdleEvent readerIdleEvent) {

        readerIdleEvent.setContext(getContext(readerIdleEvent));

        fireReaderIdleEvent(readerIdleEvent);
    }

    public abstract void fireReaderIdleEvent(ReaderIdleEvent<T> channelInactiveEvent);

    @Override
    public void writerIdle(WriterIdleEvent writerIdleEvent) {

        writerIdleEvent.setContext(getContext(writerIdleEvent));

        fireWriterIdleEvent(writerIdleEvent);
    }

    public abstract void fireWriterIdleEvent(WriterIdleEvent<T> channelInactiveEvent);

    @Override
    public void allIdle(AllIdleEvent allIdleEvent) {

        allIdleEvent.setContext(getContext(allIdleEvent));

        fireAllIdleEvent(allIdleEvent);
    }

    public abstract void fireAllIdleEvent(AllIdleEvent<T> allIdleEvent);

    private ContextWrapper getContext(AbstractEvent event) {
        return ContextCache.get(event.getChannelHandlerContext());
    }

}
