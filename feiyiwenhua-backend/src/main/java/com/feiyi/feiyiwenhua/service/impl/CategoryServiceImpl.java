package com.feiyi.feiyiwenhua.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feiyi.feiyiwenhua.entity.Category;
import com.feiyi.feiyiwenhua.repository.CategoryRepository;
import com.feiyi.feiyiwenhua.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Category> findAll() {
        return categoryRepository.selectList(null);
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.selectById(id);
    }

    @Override
    public Category save(Category category) {
        if (category.getId() != null) {
            categoryRepository.updateById(category);
        } else {
            categoryRepository.insert(category);
        }
        return category;
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    // 新增方法：根据名称查找分类
    public Category findByName(String name) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, name);
        return categoryRepository.selectOne(queryWrapper);
    }
}