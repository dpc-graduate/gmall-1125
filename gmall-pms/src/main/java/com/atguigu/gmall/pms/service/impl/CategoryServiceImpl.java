package com.atguigu.gmall.pms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public List<CategoryEntity> queryCategoriesByPid(Long id) {
        QueryWrapper<CategoryEntity> wrapper = new QueryWrapper<>();
        if (id != -1) {
            wrapper.eq("parent_id", id);
        }

        return baseMapper.selectList(wrapper);
    }
@Autowired
private CategoryMapper categoryMapper;
    @Override
    public List<CategoryEntity> queryCategoriesSubByPid(Long pid) {
        return this.categoryMapper.queryCategoriesSubByPid(pid);
    }

    @Override
    public List<CategoryEntity> queryCategoriesByCid3(Long cid3) {
        CategoryEntity level3 = this.getById(cid3);
        CategoryEntity level2 = this.getById(level3.getParentId());
        CategoryEntity level1 = this.getById(level2.getParentId());
        List<CategoryEntity> categoryEntities = Arrays.asList(level3, level2, level1);
        return categoryEntities;
    }

}