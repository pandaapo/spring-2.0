package com.spring.framework.webmvc.servlet;

import com.spring.framework.annotation.MyRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MyHandlerAdapter {
    //判断是否是MyHandlerMapping
    public boolean supports(Object handler) {return (handler instanceof MyHandlerMapping);}

    public MyModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        MyHandlerMapping handlerMapping = (MyHandlerMapping)handler;
        /** 把方法的形参列表和request的实参列表所在顺序进行一一对应 **/
        //提取方法中加了注解的形式参数
        // 把方法中的注解拿到，得到的一个二维数组。之所以是二维的，是因为一个参数可以有多个注解，而一个方法又有多个参数
        Map<String,Integer> paramIndexMapping = new HashMap<String, Integer>();
        Annotation[] [] pa = handlerMapping.getMethod().getParameterAnnotations();
        for (int i = 0; i < pa.length; i++) {
            for (Annotation annotation : pa[i]) {
                if(annotation instanceof MyRequestParam){
                    String paramName = ((MyRequestParam)annotation).value();
                    if(!"".equals(paramName.trim())){
                        paramIndexMapping.put(paramName, i);
                    }
                }
            }
        }
        //提取方法中request和response两个形式参数
        Class<?> [] paramsTypes = handlerMapping.getMethod().getParameterTypes();
        for (int i = 0; i < paramsTypes.length; i++) {
            Class<?> type = paramsTypes[i];
            if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                paramIndexMapping.put(type.getName(), i);
            }
        }

        //提取request中实际参数
        Map<String, String[]> params = request.getParameterMap();
        Object[] paramValues = new Object[paramsTypes.length];
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]","").replaceAll("\\s",",");
            if(!paramIndexMapping.containsKey(param.getKey())){continue;}
            int index = paramIndexMapping.get(param.getKey());
            paramValues[index]  = caseStringValue(value, paramsTypes[index]);
        }
        //提取把方法中的request和response两个实际参数
        if(paramIndexMapping.containsKey(HttpServletRequest.class.getName())) {
            int reqIndex = paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = request;
        }
        if(paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int respIndex = paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[respIndex] = response;
        }

        /** 调用方法**/
        Object result = handlerMapping.getMethod().invoke(handlerMapping.getController(), paramValues);
        if(result == null || result instanceof Void) {return null;}

        boolean isModelAndView = handlerMapping.getMethod().getReturnType() == MyModelAndView.class;
        if(isModelAndView){
            return (MyModelAndView)result;
        }
        return null;
    }

    // 将String类型转换成其他类型
    private Object caseStringValue(String value, Class<?> paramsType){
        if(Integer.class == paramsType){
            return Integer.valueOf(value);
        } else if (Double.class == paramsType) {
            return Double.valueOf(value);
        }
        return value;
    }
}
