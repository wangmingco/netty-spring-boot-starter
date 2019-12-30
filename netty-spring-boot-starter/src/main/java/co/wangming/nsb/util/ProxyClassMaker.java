package co.wangming.nsb.util;

import co.wangming.nsb.command.CommandProxy;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created By WangMing On 2019-12-11
 **/
@Slf4j
public enum ProxyClassMaker {

    INSTANCE;

    private ClassPool classPool;
    private Object lock = new Object();

    public void init() throws Exception {
        if (classPool != null) {
            return;
        }

        synchronized (lock) {
            if (classPool != null) {
                return;
            }
            classPool = ClassPool.getDefault();

            try {
                String jarClassPath = System.getProperty("java.class.path");
                if (!jarClassPath.endsWith(".jar")) {
                    return;
                }

                classPool.appendClassPath(new SpringClassPath());
            } catch (Exception e) {
                log.error("", e);
                throw e;
            }
        }
    }

    @Slf4j
    private static class SpringClassPath implements ClassPath {

        @Override
        public InputStream openClassfile(String classname) throws NotFoundException {
            String classPath = classname.replace(".", "/");
            classPath = classPath + ".class";
            ClassPathResource classPathResource = new ClassPathResource(classPath);
            try {
                return classPathResource.getInputStream();
            } catch (IOException e) {
                log.error("", e);
                return null;
            }
        }

        @Override
        public URL find(String classname) {
            String classPath = classname.replace(".", "/");
            classPath = classPath + ".class";

            try {
                ClassPathResource classPathResource = new ClassPathResource(classPath);
                if (classPathResource.getInputStream() != null) {
                    return new File("").toURL();
                } else {
                    return null;
                }
            } catch (IOException e) {
                return null;
            }
        }
    }

    public Class make(String targetBeanName, String proxyClassName, Class targetClass, Method targetMethod) throws Exception {
        init();

        log.info("[代理类生成] 生成代理类名称:{}, 目标bean名称:{}, 目标方法名称:{}, 目标类名称:{}", proxyClassName, targetBeanName, targetMethod.getName(), targetClass.getCanonicalName());

        // 1. 获取到接口定义
        CtClass proxyClass = null;
        try {
            proxyClass = makeProxyClasss(proxyClassName);
            log.debug("[代理类生成] 代理类生成成功:{}", proxyClass.toString());
        } catch (Exception e) {
            log.error("[代理类生成] 代理类生成失败", e);
            throw e;
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

            return proxyClass.toClass();
        } catch (final Exception e) {
            log.error("生成代理类失败", e);
            return null;
        }

    }

    /**
     * 生成代理类, 实现了 #{@link CommandProxy} 接口, 同时被 #{@link Component} 注解
     *
     * @param proxyClassName
     * @return
     * @throws NotFoundException
     */
    private CtClass makeProxyClasss(String proxyClassName) throws Exception {

        CtClass superClass = classPool.get(CommandProxy.class.getCanonicalName());
        CtClass proxyClass = classPool.makeClass(proxyClassName);
        proxyClass.setSuperclass(superClass);

        AnnotationsAttribute attr = getAnnotationsAttribute(proxyClass, Component.class.getCanonicalName());
        proxyClass.getClassFile().addAttribute(attr);

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
    private void makeProxyField(String targetBeanName, Class targetClass, CtClass proxyClass) throws Exception {
        String type = "private " + targetClass.getCanonicalName() + " " + targetBeanName + ";";

        CtField beanField = CtField.make(type, proxyClass);

        AnnotationsAttribute attr = getAnnotationsAttribute(proxyClass, Resource.class.getCanonicalName());
        beanField.getFieldInfo().addAttribute(attr);

        proxyClass.addField(beanField);

        log.debug("[代理类生成] 添加filed:{}", beanField.getName());
    }

    // 添加 Resource 属性
    private AnnotationsAttribute getAnnotationsAttribute(CtClass proxyClass, String typeName) {
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
    private void makeProxyMethod(Method targetMethod, String targetBeanName, CtClass proxyClass) throws Exception {

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

}
