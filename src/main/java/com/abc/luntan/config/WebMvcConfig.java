package com.abc.luntan.config;

import com.abc.luntan.utils.Interceptor.LoginInterceptor;
import com.abc.luntan.utils.Interceptor.RefreshLoginInterceptor;
import com.abc.luntan.utils.Interceptor.UVInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RedissonClient redissonClient;

    /**
     * Configure cross origin requests processing.
     *
     * @param registry
     * @since 4.2
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //添加映射路径
        registry.addMapping("/**")
                //是否发送Cookie
                .allowCredentials(true)
                //设置放行哪些原始域   SpringBoot2.4.4下低版本使用.allowedOrigins("*")
                .allowedOrigins("*")
                //放行哪些请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                //.allowedMethods("*") //或者放行全部
                //放行哪些原始请求头部信息
                .allowedHeaders("*")
                //暴露哪些原始请求头部信息
                .exposedHeaders("*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("添加拦截器");

        // 查看ThreadLocal是否 有user
        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns(
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                "/api", "/api-docs", "/api-docs/**", "/doc.html/**",
                "/user/sendCode",
                "/user/register",
                "/user/login",
                "/user/query/**",
                "/user/isLogin",
                "/uv/**",
                "/sonComment/getList",
                "/sonComment/detail/**",
                "/follow/or/not/**",
                "/firstComment/getNewList",
                "/firstComment/getHotList",
                "/firstComment/detail/**",
                "/category/**",
                "/article/new",
                "/article/hot",
                "/article/detail/**",
                "/article/all",
                "/article/search"
        ).order(1);
        // redis之中存入 ThreadLocal；放行所有
        registry.addInterceptor(new RefreshLoginInterceptor(redissonClient)).addPathPatterns("/**").order(0);

        registry.addInterceptor(new UVInterceptor(redissonClient)).addPathPatterns("/**").order(0);

    }



}
