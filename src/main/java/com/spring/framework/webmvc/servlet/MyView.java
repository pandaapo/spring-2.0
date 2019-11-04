package com.spring.framework.webmvc.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyView {
    public final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";

    private File viewFile;

    public MyView(File viewFile){
        this.viewFile = viewFile;
    }

    //渲染。model是传入页面的值
    public void render(Map<String,?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        StringBuffer sb = new StringBuffer();
        /** 解析文件，并将占位符替换掉 **/
        //设置为只读
        RandomAccessFile ra = new RandomAccessFile(this.viewFile,"r");
        String line = null;
        while (null != (line = ra.readLine())) {
            line = new String(line.getBytes("ISO-8859-1"),"utf-8");
            //正则规则：Y{}的字符，且花括号内不是}的多个字符
            Pattern pattern = Pattern.compile("￥\\{[^\\}]+\\}", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String paraName = matcher.group();
                paraName = paraName.replaceAll("￥\\{|\\}","");
                Object paramValue = model.get(paraName);
                if(null == paramValue) {continue;}
                line = matcher.replaceFirst(makeStringForRegExp(paramValue.toString()));
                matcher = pattern.matcher(line);
            }
            sb.append(line);
        }
        response.setCharacterEncoding("utf-8");
//        response.setContentType(DEFAULT_CONTENT_TYPE);
        response.getWriter().write(sb.toString());
    }

    //处理特殊字符
    public static String makeStringForRegExp(String str){
        return str.replace("\\","\\\\").replace("*","\\*")
                .replace("+","\\+").replace("|","\\|")
                .replace("{","\\{").replace("}","\\}")
                .replace("(","\\(").replace("\\)","\\)")
                .replace("^","\\^").replace("$","\\$")
                .replace("[","\\[").replace("]","\\]")
                .replace("?","\\?").replace(",","\\,")
                .replace(".","\\.").replace("&","\\&");
    }
}
