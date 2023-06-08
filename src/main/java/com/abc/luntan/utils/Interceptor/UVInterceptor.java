package com.abc.luntan.utils.Interceptor;

import cn.hutool.core.date.DateUtil;
import org.redisson.api.RedissonClient;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.abc.luntan.utils.RedisConstants.SYSTEM_ALL_VISIT;
import static com.abc.luntan.utils.RedisConstants.SYSTEM_DAY_VISIT;

/**
 * 系统的访问量
 */

public class UVInterceptor implements HandlerInterceptor {


    private RedissonClient redissonClient;

    public UVInterceptor() {
    }

    public UVInterceptor(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = SYSTEM_DAY_VISIT + DateUtil.today();
        //原子获得：没有就创建
        redissonClient.getAtomicLong(key).incrementAndGet();
        redissonClient.getAtomicLong(SYSTEM_ALL_VISIT).incrementAndGet();

        return true;
    }
}
