package co.wangming.nsb.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created By WangMing On 2019-12-06
 **/
@Slf4j
public class NettyServerHandler extends ByteToMessageDecoder {

    private static final int MIN_PACKAGE_SIZE = 8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {

        if (in.readableBytes() < MIN_PACKAGE_SIZE) {
            return;
        }

        in.markReaderIndex();

        // TODO 改成INT
        int messageSize = in.readByte();
        int messageId = in.readByte();
        int readableBytes = in.readableBytes();

        log.debug("接收到远端[{}]消息. messageSize:{}, readableBytes:{}", ctx.channel().remoteAddress(), messageSize, readableBytes);
        if (readableBytes < messageSize) {
            in.resetReaderIndex();
            return;
        }

        log.debug("处理远端[{}]消息", ctx.channel().remoteAddress());

        // TODO 优化, 每次都分配一块内存很浪费资源
        byte[] messageBytes = new byte[messageSize];
        in.readBytes(messageBytes);
        CommandDispatcher.dispatch(ctx, messageId, messageBytes);
    }


}
