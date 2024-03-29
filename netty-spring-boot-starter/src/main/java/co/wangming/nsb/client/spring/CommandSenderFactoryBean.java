package co.wangming.nsb.client.spring;

import co.wangming.nsb.client.command.CommandTemplate;
import co.wangming.nsb.client.netty.NettyClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created By WangMing On 2020-01-03
 **/
public class CommandSenderFactoryBean implements FactoryBean<CommandTemplate>, InitializingBean, DisposableBean {

    @Override
    public CommandTemplate getObject() {
        return new CommandTemplate();
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
        NettyClient.INSTANCE.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        NettyClient.INSTANCE.init();
    }
}
