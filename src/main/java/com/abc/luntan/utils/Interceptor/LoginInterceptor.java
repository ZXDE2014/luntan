package com.abc.luntan.utils.Interceptor;

import com.abc.luntan.utils.BadWordUtil;
import com.abc.luntan.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author
 * 拦截未登录的用户
 */

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(UserHolder.getUser() == null) {
            // 如果用户未登录，不能返回false 是不能访问接口
            log.info("拦截未登录的用户");
            throw new Exception("用户未登录");
        }


        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.clear();
    }
}
