package co.wangming.nsb.server.event;

/**
 * Created By WangMing On 2019-12-20
 **/
public interface EventHandler<T> {

    void channelActive(ChannelActiveEvent channelActiveEvent);

    void channelInactive(ChannelInactiveEvent<T> channelInactiveEvent);

    void exception(ExceptionEvent<T> exceptionEvent);

    void readerIdle(ReaderIdleEvent<T> readerIdleEvent);

    void writerIdle(WriterIdleEvent<T> writerIdleEvent);

    void allIdle(AllIdleEvent<T> allIdleEvent);

    void unknow(UnknowEvent<T> unknowEvent);

}
