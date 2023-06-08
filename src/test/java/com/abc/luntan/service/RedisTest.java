package com.abc.luntan.service;

import com.abc.luntan.service.impl.RedisServiceImpl;
import com.abc.luntan.utils.BadWordUtil;
import com.abc.luntan.utils.api.CommonResult;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisServiceImpl service;

    @Test
    public void testBadWords() {
        String[] tmp= new String[]{"daf"};
        List<String> strings = Arrays.asList(tmp);
        CommonResult result = service.updateBadwords(strings);
        System.out.println(result.toString());
        BadWordUtil instance = BadWordUtil.getInstance();
        List<String> words = instance.getBadWords("dadd ab eqabcda");
        words.forEach(System.out::println);
        System.out.println(instance.replaceBadWord("dadd ab eqabcda daf"));

    }
}
