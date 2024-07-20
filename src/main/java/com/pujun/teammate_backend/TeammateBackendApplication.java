package com.pujun.teammate_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.pujun.teammate_backend.mapper")
@EnableScheduling
public class TeammateBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TeammateBackendApplication.class, args);
    }

}