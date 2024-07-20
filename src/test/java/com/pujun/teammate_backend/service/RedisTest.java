package com.pujun.teammate_backend.service;

import com.pujun.teammate_backend.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * Redis测试类
 * 增删查
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate; //使用自定义Bean

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("pjString", "dog");
        valueOperations.set("pjInt", 1);
        valueOperations.set("pjDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUserName("pj");
        valueOperations.set("pjUser", user);

        //查
        Object pj = valueOperations.get("pjString");
        Assertions.assertTrue("dog".equals ((String) pj));
        pj = valueOperations.get("pjInt");
        Assertions.assertTrue(1 == (Integer)pj);
        pj = valueOperations.get("pjDouble");
        Assertions.assertTrue(2.0 == (Double)pj);

        //删
        redisTemplate.delete("pjInt");
    }
}
