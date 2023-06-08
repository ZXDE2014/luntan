package com.abc.luntan.service.impl;

import cn.hutool.core.date.DateUtil;
import com.abc.luntan.utils.api.CommonResult;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abc.luntan.entity.Uvcount;
import com.abc.luntan.service.UvcountService;
import com.abc.luntan.mapper.UvcountMapper;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import static com.abc.luntan.utils.RedisConstants.SYSTEM_ALL_VISIT;
import static com.abc.luntan.utils.RedisConstants.SYSTEM_DAY_VISIT;

/**
 *
 */
@Service
public class UvcountServiceImpl extends ServiceImpl<UvcountMapper, Uvcount>
    implements UvcountService{

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public CommonResult getDay() {
        String key = SYSTEM_DAY_VISIT+ DateUtil.today();
        RBucket<Object> rBucket = redissonClient.getBucket(key);
        return CommonResult.success(rBucket.get());
    }

    @Override
    public CommonResult getAll() {
        String key = SYSTEM_ALL_VISIT;
        RBucket<Object> rBucket = redissonClient.getBucket(key);
        return CommonResult.success(rBucket.get());
    }
}




