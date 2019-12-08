package co.wangming.nsb.netty;

import co.wangming.nsb.constant.MessageType;
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
        int type = in.readByte();
        int readableBytes = in.readableBytes();

        log.debug("接收到远端[{}]消息. messageType:{}, messageSize:{}, readableBytes:{}", ctx.channel().remoteAddress(), type, messageSize, readableBytes);
        if (readableBytes < messageSize) {
            in.resetReaderIndex();
            return;
        }

        MessageType messageType = MessageType.get(type);
        if (messageType == null) {
            log.error("错误的消息类型:{}", type);
            // TODO 应答客户端, 错误的消息类型
            return;
        }

        log.debug("处理远端[{}]消息", ctx.channel().remoteAddress());

        ByteBuf messageByteBuf = in.readBytes(messageSize);

        // TODO 优化, 每次都分配一块内存很浪费资源
        byte[] messageBytes = new byte[messageSize];
        messageByteBuf.getBytes(0, messageBytes);
//        byte[] messageBytes = messageByteBuf.array();

        CommandDispatcher.dispatch(ctx, messageType, messageBytes);
    }


}
