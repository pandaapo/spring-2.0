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

//Դ���е�AdvisedSupport
public class MyAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private MyAopConfig config;

    private Pattern pointCutClassPattern;

    //����ÿ�������Ͷ�Ӧ��ִ����
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
        /**������Ҫ��ǿ���������**/
        //pointCut=public .* com.spring.demo.service..*Service..*(.*) ֻ��Ҫ�жϵ�(ǰ�ĵ�4λ֮ǰ���ַ���
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\(")-4);
        //�ټ�����ȡ��com.spring.demo.service..*Service�ⲿ�֡�Ȼ����Ϊ��ȡ��������ǰ����и�class�������ټӸ�class
        pointCutClassPattern = Pattern.compile("class " + pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ") + 1));

        /**ƥ����Ҫ���ӵ�method**/
        methodCache = new HashMap<>();
        Pattern pattern = Pattern.compile(pointCut);
        try {
            //��ȡ����������з�������LogAspect������з���
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
                    //��ÿһ��������װ��MethodInterceptor�����ִ����
                    List<Object> advices = new LinkedList<>();
                    //before
                    /// ����д����ֱ��new����aspectClass.newInstance()��Spring��д�������ù���ģʽ
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

    //��ȡ����֪ͨ/���Ӻ��ִ����
    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(), method.getParameterTypes());
            cached = methodCache.get(m);
            //�������ײ��߼����Դ���������һ�����ݴ�������
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMatch() {
        return pointCutClassPattern.matcher(this.targetClass.toString()).matches();
    }
}
