package co.wangming.nsb.rpc.springboot;

import javassist.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Created By WangMing On 2017/12/29
 **/
public class NSClassGenerator {

    private static final NSClassLoader NS_CLASS_LOADER = new NSClassLoader();

    /**
     * @param clazz
     * @return
     */
    public Class implemetsInterface(Class clazz) {
        String interfaceBinaryName = clazz.getName();
        String defineClassName = interfaceBinaryName + "Impl";

        String interfaceName = interfaceBinaryName.replace(".", "/");

        byte[] classBytes = generateBytes(clazz, interfaceName);

        return NS_CLASS_LOADER.defineClass(defineClassName, classBytes);
    }


    protected byte[] generateBytes(Class interfaceClass, String interfaceName) {

        ClassPool cp = ClassPool.getDefault();

        // 1. 获取到接口定义
        CtClass interfaceCtClass = null;
        try {
            interfaceCtClass = cp.getCtClass(interfaceName);
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        // 2. 生成接口实现类
        String proxyClassName = interfaceName + "Impl";
        CtClass ctClass = cp.makeClass(proxyClassName);

        // 3. 将接口添加到接口实现类里面去
        ctClass.addInterface(interfaceCtClass);

        // 4. 在实现类里添加接口方法
        Method[] methods = interfaceClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            try {
                generateMethodCode(methods[i], ctClass, cp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            return ctClass.toBytecode();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 生成方法, 这个方法不用管, 在使用中, 我们只需要调用methodBody()就可以了
    private void generateMethodCode(Method methodToImpl, CtClass cc, ClassPool cp) throws Exception {
        String methodName = methodToImpl.getName();
        CtClass methodReturn = cp.getCtClass(methodToImpl.getReturnType().getName());

        CtClass[] parameternames = classesToCtClasses(cp, methodToImpl.getParameterTypes());
        CtClass[] exceptionnames = classesToCtClasses(cp, methodToImpl.getExceptionTypes());

        String methodBody = methodBody();

        CtMethod cm = CtNewMethod.make(methodReturn, methodName, parameternames, exceptionnames, methodBody, cc);
        cc.addMethod(cm);
    }

    private CtClass[] classesToCtClasses(ClassPool cp, Class[] parameters) throws NotFoundException {
        String[] array = new String[parameters.length];
        String[] names = Arrays.asList(parameters).stream().map(c -> c.getName()).collect(Collectors.toList()).toArray(array);
        return cp.get(names);
    }

    private String methodBody() {
        return "System.out.println(123456789987654321l);";
    }

    public static class NSClassLoader extends ClassLoader {

        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}
