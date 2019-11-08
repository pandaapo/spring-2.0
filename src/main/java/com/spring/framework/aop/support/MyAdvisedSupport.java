package com.spring.framework.aop.support;

import com.spring.framework.aop.aspect.MyAfterReturningAdviceInterceptor;
import com.spring.framework.aop.aspect.MyAfterThrowingAdviceInterceptor;
import com.spring.framework.aop.aspect.MyMethodBeforeAdviceInterceptor;
import com.spring.framework.aop.config.MyAopConfig;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//源码中的AdvisedSupport
public class MyAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private MyAopConfig config;

    private Pattern pointCutClassPattern;

    //保存每个方法和对应的执行链
    private transient Map<Method, List<Object>> methodCache;

    public MyAdvisedSupport(MyAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        /**定义需要增强的类的正则**/
        //pointCut=public .* com.spring.demo.service..*Service..*(.*) 只需要判断到(前的第4位之前的字符串
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(")-4);
        //再继续截取出com.spring.demo.service..*Service这部分。然后因为获取到的类名前面会有个class，所以再加个class
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        /**匹配需要增加的method**/
        methodCache = new HashMap<>();
        Pattern pattern = Pattern.compile(pointCut);
        try {
            //获取切面类的所有方法，即LogAspect类的所有方法
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String, Method> aspectMethods = new HashMap<>();
            for (Method m : aspectClass.getMethods()) {
                aspectMethods.put(m.getName(), m);
            }

            for (Method m : this.targetClass.getMethods()) {
                String methodString = m.toString();
                if(methodString.contains("throws")) {
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //把每一个方法包装成MethodInterceptor，组成执行链
                    List<Object> advices = new LinkedList<>();
                    //before
                    /// 这里写法是直接new，即aspectClass.newInstance()，Spring中写法的是用工厂模式
                    if(!(null == config.getAspectBefore() || "".equals(config.getAspectBefore()))){
                        advices.add(new MyMethodBeforeAdviceInterceptor(aspectMethods.get(this.config.getAspectBefore()), aspectClass.newInstance()));
                    }
                    //after
                    if(!(null == config.getAspectAfter() || "".equals(config.getAspectAfter()))){
                        advices.add(new MyAfterReturningAdviceInterceptor(aspectMethods.get(this.config.getAspectAfter()), aspectClass.newInstance()));
                    }
                    //afterThrowing
                    if(!(null == config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow()))){
                        MyAfterThrowingAdviceInterceptor throwingAdvice = new MyAfterThrowingAdviceInterceptor(aspectMethods.get(this.config.getAspectAfterThrow()), aspectClass.newInstance());
                        throwingAdvice.setThrowName(this.config.getAspectAfterThrowingName());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(m, advices);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getTarget(){
        return this.target;
    }

    //获取加入通知/增加后的执行链
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            //？？？底层逻辑，对代理方法进行一个兼容处理？？？
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
