package com.atguigu.gmall.index.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: dpc
 * @data: 2020/6/3,15:56
 */
@Aspect
@Component
public class GamllCacheAspect {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient client;
//  常见的切点表达式  @Around("execution(* com.atguigu.gmall.index.service.*.*(..))")
//切带指定注解的方法
    @Around("@annotation(com.atguigu.gmall.index.config.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取目标方法的对象 //获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取目标方法的返回值类型
        Class type = signature.getReturnType();
        //获取方法注解
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
        //缓存前缀
        String prefix = gmallCache.prefix();
        String key=prefix+ Arrays.asList(joinPoint.getArgs());
        //查缓存
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseObject(json,type);
        }
        String lock = gmallCache.lock();
        RLock fairLock = this.client.getFairLock(lock + ":" + Arrays.asList(joinPoint.getArgs()));
        fairLock.lock();
        //加分布式锁
        //再查缓存
        String json2 = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(json2)){
            fairLock.unlock();
            return JSON.parseObject(json2,type);
        }
        //执行目标方法
        Object result = joinPoint.proceed(joinPoint.getArgs());
        long timeout = gmallCache.timeout();
        int random = gmallCache.random();
        long time = timeout + new Random().nextInt(7);
        this.redisTemplate.opsForValue().set(key,JSON.toJSONString(result),time, TimeUnit.MINUTES);
        //放入缓存，释放锁
        fairLock.unlock();

        return result;
    }
}
