package co.wangming.nsb.event;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

/**
 * Created By WangMing On 2019-12-20
 **/
@Data
public class AbstractEvent<T> {

    private T context;
    private ChannelHandlerContext channelHandlerContext;
}
