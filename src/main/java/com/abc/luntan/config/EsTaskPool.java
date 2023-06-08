package com.abc.luntan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class EsTaskPool {

    @Bean
    public ThreadPoolExecutor myThreadPoll() {
        ThreadFactory springThreadFactory = new CustomizableThreadFactory("springThread-pool-");
        return new ThreadPoolExecutor(3,3,100, TimeUnit.SECONDS,new LinkedBlockingDeque<>(10), springThreadFactory);
    }
}
