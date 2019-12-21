package co.wangming.nsb.parsers;

import co.wangming.nsb.command.CommandProxy;
import co.wangming.nsb.springboot.CommandScannerRegistrar;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 这是一种特殊类型的 #{@link MessageParser}, 主要是处理未注册解析器的参数, 当前策略时直接返回空.
 *
 * 该解析器不可被Spring扫描到, 手动添加到 #{@link CommandProxy} 中. 详见 #{@link CommandScannerRegistrar#addMessageParser(AbstractBeanDefinition, Method, Map)}
 *
 * Created By WangMing On 2019-12-20
 **/
@Slf4j
public class UnknowParser implements MessageParser<byte[], Object> {

    @Override
    public void setParser(Class parameterType) {

    }

    @Override
    public Object parse(ChannelHandlerContext ctx, byte[] bytes) throws Exception {
        log.warn("当前运行到未知参数处理器, 请检查消息参数");
        return null;
    }
}
