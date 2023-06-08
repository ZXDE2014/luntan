package com.abc.luntan.controller;


import com.abc.luntan.entity.Category;
import com.abc.luntan.service.CategoryService;
import com.abc.luntan.utils.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "文章分类相关接口")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @ApiOperation(value = "获取文章分类列表")
    @GetMapping("/list")
    public CommonResult list() {
        return CommonResult.success(categoryService.list());
    }

    @ApiOperation(value = "通过查询分类")
    @GetMapping("/{id}")
    public CommonResult getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        if (category == null)
            return CommonResult.fail("Category not found！");
        return CommonResult.success(category.getCategoryName());
    }
}
