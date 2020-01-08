package co.wangming.nsb.springboot.factorybean;

import co.wangming.nsb.netty.client.CommandTemplate;
import co.wangming.nsb.netty.client.CommandTemplateHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created By WangMing On 2020-01-03
 **/
public class CommandSenderFactoryBean implements FactoryBean<CommandTemplate>, InitializingBean, DisposableBean {


    private EventLoopGroup group;
    private Bootstrap b;

    @Override
    public CommandTemplate getObject() {
        return new CommandTemplate(b);
    }

    @Override
    public Class<?> getObjectType() {
        return CommandTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void destroy() throws Exception {
        group.shutdownGracefully();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new CommandTemplateHandler());
                    }
                });

    }
}
