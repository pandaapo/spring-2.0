package com.spring.demo.action;

import com.spring.demo.service.IModifyService;
import com.spring.demo.service.IQueryService;
import com.spring.framework.annotation.MyAutowired;
import com.spring.framework.annotation.MyController;
import com.spring.framework.annotation.MyRequestMapping;
import com.spring.framework.annotation.MyRequestParam;
import com.spring.framework.webmvc.servlet.MyModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@MyController
@MyRequestMapping("/web")
public class MyAction {

    @MyAutowired
    IQueryService queryService;

    @MyAutowired
    IModifyService modifyService;

    @MyRequestMapping("/query.json")
    public MyModelAndView query(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("name") String name){
        String result = queryService.query(name);
        return out(response, result);
    }

    @MyRequestMapping("/add*.json")
    public MyModelAndView add(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("name") String name, @MyRequestParam("addr") String addr){
        try {
            String result = modifyService.add(name,addr);
            return out(response, result);
        } catch (Exception e) {
            Map<String, Object> model = new HashMap<>();
            model.put("detail", e.getMessage());
            model.put("stackTrace", Arrays.toString(e.getStackTrace()));
            return new MyModelAndView("500", model);
        }
    }

    @MyRequestMapping("/remove.json")
    public MyModelAndView remove(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return out(response, result);
    }

    @MyRequestMapping("/edit.json")
    public MyModelAndView edit(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("id") Integer id, @MyRequestParam("name") String name){
        String result = modifyService.edit(id, name);
        return out(response, result);
    }

    private MyModelAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
