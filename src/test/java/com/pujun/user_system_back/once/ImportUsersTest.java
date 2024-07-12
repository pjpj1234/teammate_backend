package com.pujun.user_system_back.once;

import com.pujun.user_system_back.entity.User;
import com.pujun.user_system_back.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 测试插入用户类
 * @author pujun
 * @since 2024/7/12
 */
@SpringBootTest
class ImportUsersTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 插入用户
     */
    @Test
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE) //初始延迟5秒 固定速率（每隔）x秒执行一次
    public void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 1000000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserAccount("test");
            user.setUserName("pj123");
            user.setGender(0);
            user.setAvatarUrl("https://s2.loli.net/2024/06/29/xaqI7Riryk3p9to.jpg");
            user.setUserPassword("12345678");
            user.setPhone("");
            user.setEmail("");
            user.setUserStatus(0);
            user.setUpdateTime(LocalDateTime.now());
            user.setUserRole(0);
            user.setValidCode("110");
            user.setTags("[]");

            //1000条 90秒
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }
}