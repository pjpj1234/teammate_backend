package com.pujun.teammate_backend;

import com.pujun.teammate_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeammateBackendApplicationTests {

    @Autowired
    UserService userService;
    @Test
    void contextLoads() {

    }

}
