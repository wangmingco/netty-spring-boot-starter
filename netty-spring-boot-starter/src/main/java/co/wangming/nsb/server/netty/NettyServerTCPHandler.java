package co.wangming.nsb.server.netty;

import co.wangming.nsb.server.command.CommandDispatcher;
import co.wangming.nsb.server.event.EventDispatcher;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created By WangMing On 2019-12-06
 **/
public class NettyServerTCPHandler extends ByteToMessageDecoder {

    private static final Logger log = LoggerFactory.getLogger(NettyServerTCPHandler.class);

    private static final int MIN_PACKAGE_SIZE = 8;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List out) throws Exception {

        if (in.readableBytes() < MIN_PACKAGE_SIZE) {
            return;
        }

        in.markReaderIndex();

        int messageId = in.readByte();
        int messageSize = in.readByte();
        int readableBytes = in.readableBytes();
        int readerIndex = in.readerIndex();

        log.debug("接收到远端[{}]消息. messageSize:{}, readableBytes:{}", ctx.channel().remoteAddress(), messageSize, readableBytes);
        if (readableBytes < messageSize) {
            in.resetReaderIndex();
            return;
        }

        log.debug("处理远端[{}]消息, messageId:{}, readerIndex:{}, messageSize:{}", ctx.channel().remoteAddress(), messageId, readerIndex, messageSize);

        ByteBuf message = in.slice(readerIndex, messageSize);
        in.readerIndex(readerIndex + messageSize);
        ByteBuffer byteBuffer = message.nioBuffer();

        ByteBuf response = CommandDispatcher.dispatch(ctx, messageId, byteBuffer);
        if (response == null) {
            return;
        }

        ctx.writeAndFlush(response).addListener(listener -> {
            if (listener.isSuccess()) {
                log.debug("消息发送成功");
            } else {
                log.error("消息发送失败", listener.cause());
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("接受自远端连接:{}", ctx.channel().remoteAddress());

        EventDispatcher.dispatchChannelActiveEvent(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("远端连接关闭:{}", ctx.channel().remoteAddress());
        EventDispatcher.dispatchChannelInactiveEvent(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            log.debug("远端连接超时, 状态:{}, 地址:{}", event.state(), ctx.channel().remoteAddress());

            switch (event.state()) {
                case READER_IDLE:
                    EventDispatcher.dispatchReaderIdleEvent(ctx);
                    break;
                case WRITER_IDLE:
                    EventDispatcher.dispatchWriterIdleEvent(ctx);
                    break;
                case ALL_IDLE:
                    EventDispatcher.dispatchAllIdleEvent(ctx);
                    break;
                default:
                    EventDispatcher.dispatchUnknowEvent(ctx);
                    break;
            }
        } else {
            EventDispatcher.dispatchUnknowEvent(ctx);
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug("远端连接发生异常:{}", ctx.channel().remoteAddress(), cause);
        EventDispatcher.dispatchExceptionEvent(ctx, cause);
    }
}
