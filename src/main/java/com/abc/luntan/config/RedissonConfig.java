package com.abc.luntan.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient redisson() {
        Config config=new Config();
        config.useSingleServer().setAddress("redis://139.224.112.5:6379").setDatabase(1).setPassword("1234").setPingConnectionInterval(10000);
        config.setCodec(new StringCodec());
        return Redisson.create(config);
    }
}
