package com.pujun.user_system_back;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.pujun.user_system_back.mapper")
@EnableScheduling
public class UserSystemBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserSystemBackApplication.class, args);
    }

}