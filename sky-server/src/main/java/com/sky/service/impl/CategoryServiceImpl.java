package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService {

   @Autowired
   private CategoryMapper categoryMapper;

   @Autowired
   private DishMapper dishMapper;

   @Autowired
   private SetmealMapper setmealMapper;

   /**
    * 分页查询
    * @param categoryPageQueryDTO
    * @return
    */
   public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
      PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
      Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
      return new PageResult(page.getTotal(), page.getResult());
   }

   /**
    * 新增分类
    * @param categoryDTO
    */
   public void addCategory(CategoryDTO categoryDTO) {
      Category category = new Category();
      //把DTO对象里的属性复制到entity对象里
      BeanUtils.copyProperties(categoryDTO, category) ;
      //设置分类状态 默认为0(禁用)
      category.setStatus(StatusConstant.DISABLE);
      //设置修改时间和创建时间
      category.setCreateTime(LocalDateTime.now());
      category.setUpdateTime(LocalDateTime.now());
      //设置修改者和创建者
      category.setCreateUser(BaseContext.getCurrentId());
      category.setUpdateUser(BaseContext.getCurrentId());
      //插入数据库
      categoryMapper.addCategory(category);
   }

   /**
    * 根据id修改分类
    * @param id
    * @param status
    */
   public void startOrStopCategory(Long id, Integer status) {
      Category category = Category.builder()
              .id(id)
              .status(status)
              .build();
      categoryMapper.update(category);
   }

   /**
    * 根据id删除分类
    * @param id
    */
   public void deleteCategory(Long id) {
      //查询当前分类下是否有菜品
      Integer count = dishMapper.countByCategoryId(id);
      if (count > 0) {
         //有菜品，不允许删除
         throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
      }
      //查询当前分类下是否有套餐
      count = setmealMapper.countByCategoryId(id);
      if (count > 0) {
         //有套餐，不允许删除
         throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
      }
      categoryMapper.deleteById(id);
   }

   @Override
   public void updateCategory(CategoryDTO categoryDTO) {
      //TODO 分类修改功能
   }
}
