package co.wangming.nsb.netty;

import io.netty.channel.ChannelHandlerContext;
import lombok.Builder;
import lombok.Data;

/**
 * Created By WangMing On 2019-12-20
 **/
@Data
@Builder
public class NettyMessage {

    private ChannelHandlerContext ctx;
    private int messageId;
    private byte[] messageBytes;
}
