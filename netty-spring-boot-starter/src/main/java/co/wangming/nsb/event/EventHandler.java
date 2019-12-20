package co.wangming.nsb.event;

/**
 * Created By WangMing On 2019-12-20
 **/
public interface EventHandler<T> {

    T channelActive(ChannelActiveEvent channelActiveEvent);

    void channelInactive(ChannelInactiveEvent<T> channelActiveEvent);

    void exceptionEvent(ExceptionEvent<T> exceptionEvent);

    void readerIdleEvent(ReaderIdleEvent<T> readerIdleEvent);

    void writerIdleEvent(WriterIdleEvent<T> readerIdleEvent);

    void allIdleEvent(AllIdleEvent<T> readerIdleEvent);

}
