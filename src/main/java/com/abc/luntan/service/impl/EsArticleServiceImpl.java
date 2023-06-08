package com.abc.luntan.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.abc.luntan.dto.EsArticleDTO;
import com.abc.luntan.entity.Article;
import com.abc.luntan.service.EsArticleRepository;
import com.abc.luntan.service.EsArticleService;
import com.abc.luntan.utils.api.CommonResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

import static com.abc.luntan.utils.EsUtil.HIGHLIGHTBUILDER;

@Service
public class EsArticleServiceImpl implements EsArticleService {


    @Autowired
    private EsArticleRepository esRepository;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public CommonResult search(String key) {
        NativeSearchQueryBuilder query = new NativeSearchQueryBuilder();
        BoolQueryBuilder builder = QueryBuilders.boolQuery();
        if(!StrUtil.isBlank(key)){
            builder.should(QueryBuilders.matchQuery("articleTitle",key))
            .should(QueryBuilders.matchQuery("articleContent",key));
        }
        NativeSearchQuery searchQuery = query.
                withQuery(builder).
                withPageable(PageRequest.of(0, 10)).
                withHighlightBuilder(HIGHLIGHTBUILDER).
                build();

        SearchHits<EsArticleDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, EsArticleDTO.class);

        return CommonResult.success(searchHits.getSearchHits());
    }

    @Override
    public void addArticle(Article article) {
        EsArticleDTO save = elasticsearchRestTemplate.save(BeanUtil.toBean(article, EsArticleDTO.class));
    }

    public CommonResult addArticleAll(List<Article> es) {
        List<EsArticleDTO> esArticleDTOS = BeanUtil.copyToList(es, EsArticleDTO.class);
        Iterable<EsArticleDTO> execute = transactionTemplate.execute((status) -> esRepository.saveAll(esArticleDTOS));
        return CommonResult.success(execute);
    }

    public boolean delete(String index) {
        return elasticsearchRestTemplate.indexOps(IndexCoordinates.of(index)).delete();
    }
}
