import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

public class JavasisstTest {

    @Test
    public void testMakeClass() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass c = classPool.makeClass("TestClass");
        byte[] clazz = c.toBytecode();

        SimpleClassLoader simpleClassLoader = new SimpleClassLoader();
        simpleClassLoader.defineClass("TestClass", clazz);
    }

    @Test
    public void testMakeClass2() throws Exception {
        ClassPool classPool = ClassPool.getDefault();
        CtClass c = classPool.makeClass("TestClass");
//        c.toClass();
    }

    public static class SimpleClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return super.defineClass(name, b, 0, b.length);
        }
    }
}
