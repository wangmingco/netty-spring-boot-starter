package co.wangming.nsb.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created By WangMing On 2020-01-02
 **/
public class CommandTemplateHandler extends SimpleChannelInboundHandler<Object> {


    @Override
    public void channelActive(ChannelHandlerContext ctx) {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        // Server is supposed to send nothing, but if it sends something, discard it.
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }


}
