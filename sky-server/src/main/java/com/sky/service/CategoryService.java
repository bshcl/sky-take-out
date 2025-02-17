package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void addCategory(CategoryDTO categoryDTO);

    /**
     * 启用或禁用分类
     * @param id
     * @param status
     */
    void startOrStopCategory(Long id, Integer status);

    /**
     * 根据id删除分类
     * @param id
     */
    void deleteCategory(Long id);

    /**
     * 修改分类
     * @param categoryDTO
     */
    void updateCategory(CategoryDTO categoryDTO);

    /**
     * 根据type查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
