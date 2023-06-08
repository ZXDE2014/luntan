package com.abc.luntan.mapper;

import com.abc.luntan.entity.Article;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Entity com.abc.luntan.entity.Article
 */
public interface ArticleMapper extends BaseMapper<Article> {
    // mybatisplus 分页 必须将page作为参数传入
    IPage<Article> listJoinInfoPages(IPage<Article> page, @Param(Constants.WRAPPER) QueryWrapper<Article> query);

    Article queryDetail( @Param(Constants.WRAPPER) QueryWrapper<Article> query);
}




