import co.wangming.nsb.netty.NettyConfig;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created By WangMing On 2019-12-09
 **/
public class NettyConfigGenerate {


    @Test
    public void printNettyConfigMethods() {

        for (Method method : NettyConfig.class.getDeclaredMethods()) {
            if (!method.getName().startsWith("get")) {
                continue;
            }

            System.out.println("setOption(b, NettyConfig." + method.getName() + "());");
        }
    }

    @Test
    public void print() {

        for (Method method : NettyConfig.class.getDeclaredMethods()) {
            if (!method.getName().startsWith("set")) {
                continue;
            }

            String name = method.getName().substring(3).toUpperCase();
            System.out.println("NettyConfig." + method.getName() + "(springBootNettyProperties.get" + name + "());");
        }
    }
}
