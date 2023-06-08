package com.abc.luntan.service;

import com.abc.luntan.dto.ArticleDTO;
import com.abc.luntan.entity.Article;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface ArticleService extends IService<Article> {

    CommonResult queryNewArticle(Integer current, Integer category);

    CommonResult queryHotArticle(Integer current, Integer category);

    CommonResult likeArticle(Long articleId);

    CommonResult createArticle(ArticleDTO articleDTO);

    CommonResult detailArticle(Long id);

    CommonResult allArticle(String useId, Integer current);
}
