package com.pujun.user_system_back.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pujun.user_system_back.entity.User;
import com.pujun.user_system_back.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 数据预热
 * 先把重要用户的数据放入redis里，提高搜索速率
 */
@Component //设置为Bean
//@Component 是一个通用的注解，用来标识一个类作为Spring组件，
// 即将这个类注册到Spring应用上下文中，Spring会自动扫描并注册标记了@Component 的类。
@Slf4j //log注解
public class PreCacheJob {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserService userService;

    @Autowired
    private RedissonClient redissonClient;

    List<Long> mainUserList = Arrays.asList(1L); // 可以动态传入主要的用户

    @Scheduled(cron = "0 54 * * * ? ") //定时启动 cron表达式 百度搜索，六位
    public void doCacheRecommendUser(){
        RLock lock = redissonClient.getLock("teammate:precachejob:docache:lock"); //锁redis定义

        try {
            if(lock.tryLock(0, -1, TimeUnit.MILLISECONDS)){ // 等待时间为0 立即抢 释放时间设为-1 redisson看门狗机制自动 续期
                System.out.println("getLock: " + Thread.currentThread().getName());
                Thread.sleep(30000); // 休眠模拟方法时间，锁可以续期
                for (Long userId:  mainUserList) { //每个预热用户
                    String redisKey = String.format("teammate:recommend:userId:%s", userId); //键
                    Page<User> userPage = userService.page(new Page<>(1, 10), null); //值
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

                    try { //millisecond
                        valueOperations.set(redisKey, userPage, 30000, TimeUnit.MILLISECONDS); //30秒过期 不然内存一直增加 泄漏
                    } catch (Exception e) {
                        log.error("redis set key error" + e); //写入失败依然要返回数据
                    }
                }
            }
        } catch (Exception e) {
            log.error("docacheError: " + e);
        } finally { //最后 只能释放自己的锁
            if(lock.isHeldByCurrentThread()){
                System.out.println("unlock: " + Thread.currentThread().getName());
                lock.unlock();
            }
        }
    }
}
