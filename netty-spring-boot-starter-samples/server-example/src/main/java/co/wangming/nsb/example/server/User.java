package co.wangming.nsb.example.server;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created By WangMing On 2019-12-22
 **/
public class User {

    private ChannelHandlerContext channelHandlerContext;

    public void setChannelHandlerContext(ChannelHandlerContext channelHandlerContext) {
        this.channelHandlerContext = channelHandlerContext;
    }

    public ChannelHandlerContext getChannelHandlerContext() {
        return channelHandlerContext;
    }

    @Override
    public String toString() {
        return "User Address:" + channelHandlerContext.channel().remoteAddress();
    }
}
