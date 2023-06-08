package com.abc.luntan.service;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.RandomUtil;
import com.abc.luntan.utils.api.CommonResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@SpringBootTest
public class QiNiuServiceTest {
    @Autowired
    private IQiNiuService qiNiuService;

    @Autowired
    private EsArticleRepository esArticleRepository;

    @Test
    void testUpload() throws Exception {
        File file = new File("C:\\Users\\zhangxue\\Desktop\\tmp\\2023-05-03-2时20分53.csv");
        String format = DateUtil.format(DateUtil.date(), "yyyy/MM/");
        String type = FileTypeUtil.getType(file);
        String name = "122231341" + RandomUtil.randomString(10 - "1241".length()) + "." + type;
        String key = "article/" + format + name;
        CommonResult result = qiNiuService.uploadFile(file, key);
        Thread.sleep(15000L);
        qiNiuService.delete(key);
        System.out.println(result);
    }


}
