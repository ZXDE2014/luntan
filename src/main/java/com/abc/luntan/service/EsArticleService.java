package com.abc.luntan.service;

import com.abc.luntan.entity.Article;
import com.abc.luntan.utils.api.CommonResult;


public interface EsArticleService {

    CommonResult search(String key);

    void addArticle(Article article);
}
