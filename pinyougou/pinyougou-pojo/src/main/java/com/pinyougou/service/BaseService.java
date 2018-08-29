package com.pinyougou.service;

import com.pinyougou.vo.PageResult;

import java.io.Serializable;
import java.util.List;

public interface BaseService<T> {
    /**
     * 根据主键查询
     * @param id 主键
     * @return 实体对象
     */
    public T findOne(Serializable id);

    /**
     * 查询全部
     * @return 实体对象集合
     */
    public List<T> findAll();

    /**
     * 根据条件查询列表
     * @param t 查询条件对象      * @return
     */
    public List<T> findByWhere(T t);

    /**
     * 分页查询列表
     * @param page 页号
     * @param rows 页大小
     * @return 分页实体对象
     */
    public PageResult findByPage(Integer page, Integer rows);

    /**
     * 根据条件分页查询列表
     * @param page 页号
     * @param rows 页大小
     * @param t    查询条件对象
     * @return 分页实体对象
     */
    public PageResult findPage(Integer page, Integer rows, T t);

    /**
     * 新增
     * @param t 实体对象
     */


    public void add(T t);

    /**
     * 根据主键更新
     * @param t 实体对象
     */
    public void update(T t);

    /**
     * 批量删除
     * @param ids 主键集合
     */
    public void deleteByIds(Serializable[] ids);
}


