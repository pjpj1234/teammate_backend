package com.pujun.user_system_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.pujun.user_system_back.mapper")
public class UserSystemBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserSystemBackApplication.class, args);
    }

}