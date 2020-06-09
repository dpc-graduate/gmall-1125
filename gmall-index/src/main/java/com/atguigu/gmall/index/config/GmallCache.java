package com.atguigu.gmall.index.config;

import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * @author: dpc
 * @data: 2020/6/3,15:45
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    /**
     * 缓存前缀
     *
     * @return
     */
    String prefix() default "";

    /**
     * 分布式锁,防止缓存击穿
     */
    String lock() default "lock";

    /**
     * 缓存时间
     */
    long timeout() default 60L;

    /**
     * 防止缓存雪崩，添加过期时间的随机值
     */
    int random() default 0;
}
