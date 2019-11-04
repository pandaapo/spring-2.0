package com.spring.demo.service.impl;

import com.spring.demo.service.IModifyService;
import com.spring.framework.annotation.MyService;

@MyService
public class ModifyService implements IModifyService {
    @Override
    public String add(String name, String addr) throws Exception{
        if("error".equals(name)){
            throw new Exception("想看下500页面，故意写的异常");
        }
        return "modifyService add,name=" +name+ ",addr=" +addr;
    }

    @Override
    public String edit(Integer id, String name) {
        return "modifyService edit,id=" +id+ ",name=" + name;
    }

    @Override
    public String remove(Integer id) {
        return "modifyService id=" +id;
    }
}
