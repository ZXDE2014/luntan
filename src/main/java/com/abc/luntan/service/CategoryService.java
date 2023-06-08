package com.abc.luntan.service;

import com.abc.luntan.entity.Category;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface CategoryService extends IService<Category> {
    public CommonResult getCategoryList();
    public CommonResult getCategoryById(Integer id);
}
