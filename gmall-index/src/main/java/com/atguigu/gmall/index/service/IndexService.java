package com.atguigu.gmall.index.service;


import ch.qos.logback.core.util.TimeUtil;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.index.config.GmallCache;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: dpc
 * @data: 2020/6/2,13:50
 */
@Service
public class IndexService {
    @Autowired
    private GmallPmsClient pmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    private static final String KEY_PREFIX = "index:cats:";

    public List<CategoryEntity> queryLevelOneCategories() {
        //一级分类
        List<CategoryEntity> categoryEntities = pmsClient.queryCategoriesByPid(0L).getData();
        //TODO:
        return categoryEntities;
    }

    @GmallCache(prefix = KEY_PREFIX,lock = "lock",timeout =129600L,random = 10080)
    public List<CategoryEntity> querySubLevelTwo(Long pid) {
        List<CategoryEntity> categoryEntityList = pmsClient.queryCategoriesSubByPid(pid).getData();
        return categoryEntityList;
    }

    public List<CategoryEntity> querySubLevelTwo2(Long pid) {
        //1查询缓存，有则直接返回
        String json = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json)) {
            return JSON.parseArray(json, CategoryEntity.class);
        }
        //2没有就查询数据库放入缓存
        //避免穿透，避免雪崩
        //为了防止缓存击穿，添加分布式锁
        RLock fairLock = this.redissonClient.getFairLock("lock" + pid);
        fairLock.lock();
        //再次确认缓存中是否有数据
        //原因： 1加锁的过程中可能会有其他数据加入到缓存中，
        //      2高并发情况下，第一个请求获取到锁之后，发送远程请求查询数据并放入缓存中，其他的请求就可以不再远程调用，直接查询缓存即可
        String json2 = this.redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json2)) {
            fairLock.unlock();
            return JSON.parseArray(json2, CategoryEntity.class);
        }
        List<CategoryEntity> categoryEntityList = pmsClient.queryCategoriesSubByPid(pid).getData();
        this.redisTemplate.opsForValue().set(KEY_PREFIX + pid, JSON.toJSONString(categoryEntityList), 3 * 30 * 24 * 60 + new Random().nextInt(7), TimeUnit.DAYS);
        fairLock.unlock();
        return categoryEntityList;
    }

    public void testLock() {
        String uuid = UUID.randomUUID().toString();
        Boolean flag = this.redisTemplate.opsForValue().setIfAbsent("lock", uuid, 3, TimeUnit.SECONDS);
        if (flag) {
            //读取redis中的num
            String num = this.redisTemplate.opsForValue().get("num");
            if (StringUtils.isBlank(num)) {
                return;
            }
            int i = Integer.parseInt(num);
            //++
            i++;
            //放入redis
            this.redisTemplate.opsForValue().set("num", String.valueOf(i));
            //防止误删，确保删除的是自己的锁
            String script = "if redis.call('get', KEYS[1]) == ARGV[1]" +
                    " then return redis.call('del', KEYS[1]) " +
                    "else return 0 end";
            //利用LUA脚本实现判断删除的原子性，防止误删，因为下面的代码不具备原子性
            this.redisTemplate.execute(new DefaultRedisScript<>(script), Arrays.asList("lock"), uuid);
      /*
            if (StringUtils.equals(this.redisTemplate.opsForValue().get("lock"), uuid)) {
                this.redisTemplate.delete("lock");
            }
       */
        } else {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            testLock();
        }
    }

    public void testLock2() {
        RLock lock = this.redissonClient.getLock("lock");
        //加锁
        lock.lock();
        String num = this.redisTemplate.opsForValue().get("num");
        if (StringUtils.isBlank(num)) {
            return;
        }

        int i = Integer.parseInt(num);
        //++
        i++;
        //放入redis
        this.redisTemplate.opsForValue().set("num", String.valueOf(i));
        //释放锁
        lock.unlock();
    }

    public String read() {
        RReadWriteLock rwlock = this.redissonClient.getReadWriteLock("rwlock");
        rwlock.readLock().lock();
        rwlock.readLock().unlock();
        return "测试读锁！";


    }

    public String writer() {
        RReadWriteLock rwlock = this.redissonClient.getReadWriteLock("rwlock");
        rwlock.writeLock().lock();
        rwlock.writeLock().unlock();
        return "测试写锁！";
    }

    public String latch() throws InterruptedException {
        RCountDownLatch latch = this.redissonClient.getCountDownLatch("latch");
        latch.trySetCount(6);
        latch.wait();
        return "结束！";


    }

    public String down() {
        RCountDownLatch latch = this.redissonClient.getCountDownLatch("latch");
        latch.countDown();
        return "减一！";
    }
}
