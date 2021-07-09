package co.wangming.nsb.server.event;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created By WangMing On 2019-12-20
 **/
public class AbstractEvent<T> {

    private T context;
    private ChannelHandlerContext channelHandlerContext;

    public T getContext() {
        return context;
    }

    public void setContext(T context) {
        this.context = context;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }
}
