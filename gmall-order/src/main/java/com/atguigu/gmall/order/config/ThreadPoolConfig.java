package com.atguigu.gmall.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: dpc
 * @data: 2020/6/9,11:21
 */
@Configuration
@RefreshScope
public class ThreadPoolConfig {
    /**
     * @param coreSize
     * @param maxSize
     * @param timeout
     * @param blockingSize
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(
            @Value("${ThreadPool.coreSize}") Integer coreSize,
            @Value("${ThreadPool.maxSize}") Integer maxSize,
            @Value("${ThreadPool.timeout}") Integer timeout,
            @Value("${ThreadPool.blockingSize}") Integer blockingSize
    ){
        return new ThreadPoolExecutor(coreSize, maxSize, timeout, TimeUnit.SECONDS, new ArrayBlockingQueue<>(blockingSize));
    }
}
