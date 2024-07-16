package com.pujun.user_system_back.job;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pujun.user_system_back.entity.User;
import com.pujun.user_system_back.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j //log注解
public class PreCacheJob {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserService userService;

    List<Long> mainUserList = Arrays.asList(1L); // 可以动态传入主要的用户

    @Scheduled(cron = "0 6 * ? * ?") //定时启动 cron表达式 百度搜索，六位
    public void doCacheRecommendUser(){
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
}
