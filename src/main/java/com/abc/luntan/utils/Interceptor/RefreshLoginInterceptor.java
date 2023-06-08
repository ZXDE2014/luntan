package com.abc.luntan.utils.Interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.abc.luntan.dto.UserDTO;
import com.abc.luntan.utils.RedisConstants;
import com.abc.luntan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @author
 * 1.去redis查看用户是否已经登录
 * 2.如果登录，想用户信息存入UserHolder
 * 3.放行
 */
@Slf4j
public class RefreshLoginInterceptor implements HandlerInterceptor {


    private RedissonClient redissonClient;

    public RefreshLoginInterceptor() {
    }

    public RefreshLoginInterceptor(RedissonClient redissonClient){
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("Refresh Interceptor preHandle");
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return true;
        }
        String key = RedisConstants.LOGIN_USER_KEY+token;
        RMap<Object, Object> userMap = redissonClient.getMap(key);

        if (userMap.isEmpty()) {
            return true;
        }

        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        UserHolder.setUser(userDTO);

        redissonClient.getMap(key).expire(RedisConstants.LOGIN_USER_TTL, TimeUnit.HOURS);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.clear();
    }
}
