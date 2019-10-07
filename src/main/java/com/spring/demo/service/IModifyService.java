package com.spring.demo.service;

/**
 * 增删改业务
 */
public interface IModifyService {

    /**
     * 增加
     * @param name
     * @param addr
     * @return
     */
    public String add(String name, String addr);

    /**
     * 修改
     * @param id
     * @param name
     * @return
     */
    public String edit(Integer id, String name);

    /**
     * 删除
     * @param id
     * @return
     */
    public String remove(Integer id);
}
