package com.spring.framework.webmvc.servlet;

import java.io.File;
import java.util.Locale;

public class MyViewResolver {
    private File templateRootDir;
    //默认的文件后缀
    private final String DEFAULT_TEMPLATE_SUFFX = ".html";

    public MyViewResolver(String templateRoot) {
        //？？？templateRoot和templateRootPath的区别？？？
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
        //变成文件路径
        templateRootDir = new File(templateRootPath);
    }

    MyView resolveViewName(String viewName, Locale locale){
        if(null == viewName || "".equals(viewName.trim())) {
            return null;
        }
        viewName = viewName.endsWith(DEFAULT_TEMPLATE_SUFFX) ? viewName : (viewName + ".html");
        File templateFile = new File((templateRootDir.getPath() + "/" + viewName).replaceAll("/+","/"));
        return new MyView(templateFile);
    }
}
