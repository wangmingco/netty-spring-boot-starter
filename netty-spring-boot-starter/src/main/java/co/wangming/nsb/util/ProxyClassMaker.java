package co.wangming.nsb.util;

import co.wangming.nsb.netty.CommandProxy;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created By WangMing On 2019-12-11
 **/
@Slf4j
public class ProxyClassMaker {

    private static final NSClassLoader NS_CLASS_LOADER = new NSClassLoader();

    public static Class make(String targetBeanName, String proxyClassName, Class targetClass, Method targetMethod) {

        log.info("[代理类生成] 生成代理类名称:{}, 目标bean名称:{}, 目标方法名称:{}, 目标类名称:{}", proxyClassName, targetBeanName, targetMethod.getName(), targetClass.getCanonicalName());

        byte[] classBytes = makeProxyClassBytes(targetBeanName, proxyClassName, targetClass, targetMethod);
        if (classBytes == null || classBytes.length == 0) {
            return null;
        }

        return NS_CLASS_LOADER.defineClass(proxyClassName, classBytes);
    }


    private static byte[] makeProxyClassBytes(String targetBeanName, String proxyClassName, Class targetClass, Method targetMethod) {

        ClassPool cp = ClassPool.getDefault();

        // 1. 获取到接口定义
        CtClass proxyClass = null;
        try {
            proxyClass = makeProxyClasss(proxyClassName, cp);
        } catch (NotFoundException e) {
            log.error("[代理类生成] 生成class失败", e);
            return null;
        }

        // 2. 添加spring bean
        try {
            makeProxyField(targetBeanName, targetClass, proxyClass);
        } catch (Exception e) {
            log.error("[代理类生成] 为代理bean添加targetBean失败", e);
            return null;
        }

        // 3. 添加代理方法
        try {
            makeProxyMethod(targetMethod, targetBeanName, proxyClass);
        } catch (Exception e) {
            log.error("[代理类生成] 为代理bean添加代理方法失败", e);
            return null;
        }

        try {
            log.debug("[代理类生成] 代理类生成完成:{}", proxyClass.toString());

            return proxyClass.toBytecode();
        } catch (final Exception e) {
            log.error("生成代理类失败", e);
            return null;
        }

    }

    /**
     * 生成代理类, 实现了 #{@link CommandProxy} 接口, 同时被 #{@link Component} 注解
     *
     * @param proxyClassName
     * @param cp
     * @return
     * @throws NotFoundException
     */
    private static CtClass makeProxyClasss(String proxyClassName, ClassPool cp) throws NotFoundException {
        CtClass proxyInterface = cp.get(CommandProxy.class.getCanonicalName());
        CtClass proxyClass = cp.makeClass(proxyClassName);
        proxyClass.setInterfaces(new CtClass[]{proxyInterface});

        AnnotationsAttribute attr = getAnnotationsAttribute(proxyClass, Component.class.getCanonicalName());
        proxyClass.getClassFile().addAttribute(attr);

        log.debug("[代理类生成] 代理类生成:{}", proxyClass.toString());
        return proxyClass;
    }

    /**
     * 生成目标属性字段, 在代理方法中调用该目标属性的目标方法, 同时为其添加 #{@link Resource} 注解
     *
     * @param targetBeanName
     * @param targetClass
     * @param proxyClass
     * @throws Exception
     */
    private static void makeProxyField(String targetBeanName, Class targetClass, CtClass proxyClass) throws Exception {
        String type = "private " + targetClass.getCanonicalName() + " " + targetBeanName + ";";

        CtField beanField = CtField.make(type, proxyClass);

        AnnotationsAttribute attr = getAnnotationsAttribute(proxyClass, Resource.class.getCanonicalName());
        beanField.getFieldInfo().addAttribute(attr);

        proxyClass.addField(beanField);

        log.debug("[代理类生成] 添加filed:{}", beanField.getName());
    }

    // 添加 Resource 属性
    private static AnnotationsAttribute getAnnotationsAttribute(CtClass proxyClass, String typeName) {
        ClassFile cfile = proxyClass.getClassFile();
        ConstPool cpool = cfile.getConstPool();

        AnnotationsAttribute attr = new AnnotationsAttribute(cpool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(typeName, cpool);
        attr.addAnnotation(annot);
        return attr;
    }

    /**
     * 生成代理方法, 该代理方法实现自 #{@link CommandProxy#invoke(List)} 方法
     *
     * @param targetMethod
     * @param targetBeanName
     * @param proxyClass
     * @throws Exception
     */
    private static void makeProxyMethod(Method targetMethod, String targetBeanName, CtClass proxyClass) throws Exception {

        String methodBody = "\n" +
                "    public java.lang.Object invoke(java.util.List paramters) {\n" +
                "        ${methodBody}\n" +
                "    }";

        String methodInvoke = "${targetBeanName}.${targetMethod}(${params});";
        methodInvoke = methodInvoke.replace("${targetBeanName}", targetBeanName);
        methodInvoke = methodInvoke.replace("${targetMethod}", targetMethod.getName());

        List<String> args = new ArrayList<>();
        int i = 0;
        for (Parameter parameter : targetMethod.getParameters()) {
            Class<?> type = parameter.getType();
            args.add("(" + type.getCanonicalName() + ")paramters.get(" + i++ + ")");
        }

        String methodParams = args.stream().collect(Collectors.joining(","));
        methodInvoke = methodInvoke.replace("${params}", methodParams);

        Class<?> returnType = targetMethod.getReturnType();
        String methodRetuen = "";
        if (Void.TYPE.equals(returnType)) {
            methodRetuen = methodInvoke + " return null;";
        } else {
            methodRetuen = "return " + methodInvoke;
        }

        methodBody = methodBody.replace("${methodBody}", methodRetuen);
        log.debug("[代理类生成] 代理方法生成\n  {}", methodBody);

        CtMethod cm = CtNewMethod.make(methodBody, proxyClass);

        proxyClass.addMethod(cm);
    }

    public static class NSClassLoader extends ClassLoader {

        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

}
