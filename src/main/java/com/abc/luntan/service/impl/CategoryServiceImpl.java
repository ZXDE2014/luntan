package com.abc.luntan.service.impl;

import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.Category;
import com.abc.luntan.service.CategoryService;
import com.abc.luntan.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{
    @Override
    public CommonResult getCategoryList() {
        return CommonResult.success(this.list());
    }

    @Override
    public CommonResult getCategoryById(Integer id) {
        return CommonResult.success(this.getById(id).getCategoryName());
    }
}




