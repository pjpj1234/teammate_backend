package com.pujun.teammate_backend.once;

import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;


/**
 * 插入用户
 * @author pujun
 * @since 2024/7/12
 */
@Component
public class ImportUsers {

    @Autowired
    private UserMapper userMapper;

    /**
     * 插入用户
     */
    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE) //初始延迟5秒 固定速率（每隔）x秒执行一次
    public void doInsertUsers(){
        StopWatch stopWatch= new StopWatch();
        stopWatch.start(); // 定时器
        System.out.println("aaa");
//        final int INSERT_NUM = 100000;
//        for (int i = 0; i < INSERT_NUM; i++) {
//            User user = new User();
//            user.setUserAccount("test");
//            user.setUserName("pj123");
//            user.setGender(0);
//            user.setAvatarUrl("https://s2.loli.net/2024/06/29/xaqI7Riryk3p9to.jpg");
//            user.setUserPassword("12345678");
//            user.setPhone("");
//            user.setEmail("");
//            user.setUserStatus(0);
//            user.setUpdateTime(LocalDateTime.now());
//            user.setUserRole(0);
//            user.setValidCode("110");
//            user.setTags("[]");

//            userMapper.insert(user);
//        }
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }
}
