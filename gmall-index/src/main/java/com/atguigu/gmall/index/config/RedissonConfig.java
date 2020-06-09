package com.atguigu.gmall.index.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: dpc
 * @data: 2020/6/3,14:09
 */
@Configuration
public class RedissonConfig {
    @Bean
    public RedissonClient client() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://212.64.52.80:6379");
        return Redisson.create(config);
    }
}
