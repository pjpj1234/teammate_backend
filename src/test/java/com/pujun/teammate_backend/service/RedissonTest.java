package com.pujun.teammate_backend.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){
        // list, 数据存在本地 JVM 内存中
        List<String> list = new ArrayList<>();
        list.add("pj");
        System.out.println("list: " + list.get(0));

        list.remove(0);

        // 数据存在 Redis 的内存中
        RList<String> rList = redissonClient.getList("test-list");
        rList.add("redisson-pj");
        System.out.println("rList: " + rList.get(0));
        rList.remove(0);

        // map
        Map<String, Integer> map = new HashMap<>();
        map.put("map-pj", 10);
        map.get("map-pj");

        RMap<Object, Object> map1 = redissonClient.getMap("test-mao");
        // set

        // stack
    }
}
