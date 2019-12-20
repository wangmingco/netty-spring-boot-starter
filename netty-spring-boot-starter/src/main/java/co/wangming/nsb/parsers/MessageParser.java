package co.wangming.nsb.parsers;

import io.netty.channel.ChannelHandlerContext;

/**
 * Created By WangMing On 2019-12-20
 **/
public interface MessageParser<T, U> {

    void setParser(Class v);

    U parse(ChannelHandlerContext ctx, T t) throws Exception;
}
