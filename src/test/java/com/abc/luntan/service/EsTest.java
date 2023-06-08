package com.abc.luntan.service;

import cn.hutool.core.date.DateUtil;
import com.abc.luntan.dto.EsArticleDTO;
import com.abc.luntan.service.EsArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@SpringBootTest
public class EsTest {

    @Autowired
    private EsArticleRepository repository;

    @Test
    public void testSave() throws Exception {
        System.out.println("kais");

        EsArticleDTO save = repository.save(new EsArticleDTO(
            1,"worde","Elasticsearch 设置用户名密码认证(亲测)\n" +
                "4天前 2、重启ES,查看下索引,发现多了一个.security-7索引,将其删除 3、到此就回到ES没有设置密码的阶段了,如果想重新设置密码,请从第一步开始 Elasticsearch安装不会默认开启...",
                DateUtil.date(),
                1,
                "word"
        ));
    }

}
