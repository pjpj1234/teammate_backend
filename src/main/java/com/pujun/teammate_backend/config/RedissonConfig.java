package com.pujun.teammate_backend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    @Bean
    //@Bean 注解通常用在方法上，用来声明一个由Spring容器管理的Bean。使用@Bean注解的方法会在调用时创建一个对象，
    // 并将其放入Spring容器中，以便在需要时可以被注入到其他Bean中。
    public RedissonClient redissonClient(){
        // 1. Create config object
        Config config = new Config();
        String redisAddress = String.format("redis://%s:%s",host,port); //写成动态的 不用写死
        // 使用单个Redis，没有开集群 useClusterServers 设置地址和使用库
        config.useSingleServer().setAddress(redisAddress).setDatabase(3);

        // 2. Create Redisson instance
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
