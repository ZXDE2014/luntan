package com.abc.luntan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.abc.luntan.service.IRedisService;
import com.abc.luntan.utils.BadWordUtil;
import com.abc.luntan.utils.api.CommonResult;
import org.redisson.api.RFuture;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.abc.luntan.utils.RedisConstants.SYSTEM_BADWORD;

@Service
public class RedisServiceImpl  implements IRedisService {

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public CommonResult updateBadwords(List<String> badwords) {
        String key = SYSTEM_BADWORD;
        RSet<String> set = redissonClient.getSet(key);
        RFuture<Boolean> future = set.addAllAsync(badwords);
        BadWordUtil instance = BadWordUtil.getInstance();
        instance.setKeywords(new ArrayList<>(set));
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            return CommonResult.fail("Badwords NOT updated");
        }
        return  CommonResult.success("Badwords updated") ;

    }
}
