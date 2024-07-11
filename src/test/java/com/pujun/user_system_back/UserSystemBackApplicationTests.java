package com.pujun.user_system_back;

import com.pujun.user_system_back.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserSystemBackApplicationTests {

    @Autowired
    UserService userService;
    @Test
    void contextLoads() {

    }

}
