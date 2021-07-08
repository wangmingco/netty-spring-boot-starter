import co.wangming.nsb.command.CommandProxy;
import co.wangming.nsb.util.CommandProxyMaker;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created By WangMing On 2019-12-11
 **/
public class CommandProxyMakerTest {

    @Test
    public void testMakeClass() throws Exception {
        Class targetClass = TestClass.class;
        Method printMethod = targetClass.getMethod("print1");

        Class proxyClass = makeClass(targetClass, printMethod);

        Assert.assertEquals(0, proxyClass.getInterfaces().length);
        Assert.assertEquals(CommandProxy.class.getCanonicalName(), proxyClass.getSuperclass().getTypeName());
        Assert.assertNotNull(proxyClass.getAnnotation(Component.class));
    }

    private Class makeClass(Class targetClass, Method printMethod) throws Exception {
        String beanName = "testClass";
        String commandMappingName = beanName + "$$" + CommandProxy.class.getSimpleName() + "$$" + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt();

        try {
            return CommandProxyMaker.INSTANCE.make(beanName, commandMappingName, targetClass, printMethod);
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void testGetField() throws Exception {
        Class targetClass = TestClass.class;
        Method printMethod = targetClass.getMethod("print1");

        Class proxyClass = makeClass(targetClass, printMethod);

        Field testClassField = proxyClass.getDeclaredField("testClass");
        Assert.assertEquals(targetClass, testClassField.getType());
        Assert.assertEquals("testClass", testClassField.getName());
        Assert.assertEquals(1, testClassField.getAnnotations().length);
        Assert.assertEquals(Autowired.class, testClassField.getAnnotations()[0].annotationType());
    }

    @Test
    public void testMethodPrint1() throws Exception {
        Class targetClass = TestClass.class;
        Method printMethod = targetClass.getMethod("print1");

        Class proxyClass = makeClass(targetClass, printMethod);

        Method print1Method = proxyClass.getMethod("invoke", List.class);
        Assert.assertNotNull(print1Method);
        Assert.assertEquals(Object.class, print1Method.getReturnType());
    }

    public static class TestClass {

        public void print1() {

        }

        public void print2(String args) {
        }

        public String print3() {
            return "";
        }

        public String print4(String args) {
            return args;
        }
    }
}
