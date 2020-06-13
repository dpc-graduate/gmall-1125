package com.atguigu.gmall.cart.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: dpc
 * @data: 2020/6/10,21:26
 */
@Configuration
public class CartAsyncConfig implements AsyncConfigurer {
    @Autowired
    private CartAsyncExceptionHandler cartAsyncExceptionHandler;
    /**
     * 自定义线程池
     * @return
     */
    @Override
    public Executor getAsyncExecutor() {
        //return new ThreadPoolExecutor();
        return null;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return cartAsyncExceptionHandler;
    }
}
