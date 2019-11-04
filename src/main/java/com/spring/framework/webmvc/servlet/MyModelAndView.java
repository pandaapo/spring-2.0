package com.spring.framework.webmvc.servlet;

import java.util.Map;

public class MyModelAndView {
    private String viewName;
    private Map<String,?> model;

    public String getViewName() {
        return viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public MyModelAndView(String viewName) {
        this.viewName = viewName;
    }

    public MyModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }
}
