package com.pujun.teammate_backend.once;

import com.pujun.teammate_backend.entity.User;
import com.pujun.teammate_backend.mapper.UserMapper;
import com.pujun.teammate_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

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

    @Autowired
    private UserService userService;

    /**
     * 循环插入用户 10000 条 耗时 21.808秒
     */
    @Test
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE) //初始延迟5秒 固定速率（每隔）x秒执行一次
    public void doInsertUsers1() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 10000;
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserAccount("假用户");
            user.setUserName("pj123");
            user.setGender(0);
            user.setAvatarUrl("https://s2.loli.net/2024/06/29/xaqI7Riryk3p9to.jpg");
            user.setUserPassword("12345678");
            user.setPhone("110");
            user.setEmail("110@qq.com");
            user.setUserStatus(0);
            user.setUpdateTime(LocalDateTime.now());
            user.setUserRole(0);
            user.setValidCode("110");
            user.setTags("[]");

            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    /**
     * 批量插入用户 10000 耗时：5.7秒
     */
    @Test
    public void doInsertUsers2(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 10000;
        ArrayList<User> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserAccount("假用户2");
            user.setUserName("pj123");
            user.setGender(0);
            user.setAvatarUrl("https://s2.loli.net/2024/06/29/xaqI7Riryk3p9to.jpg");
            user.setUserPassword("12345678");
            user.setPhone("110");
            user.setEmail("110@qq.com");
            user.setUserStatus(0);
            user.setUpdateTime(LocalDateTime.now());
            user.setUserRole(0);
            user.setValidCode("110");
            user.setTags("[]");

            userList.add(user);
        }
        userService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }

    /**
     * 批量并发插入用户
     */
    @Test
    public void doConcurrencyInsertUsers3(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100000; //插入量
        int batchSize = 5000;//批量数据量
        int j = 0;

        // 自定义线程池
        ExecutorService executorService = new ThreadPoolExecutor(
                21, //核心线程数
                1000, //最大线程数
                10000, //非核心线程空闲时的存活时间
                TimeUnit.MINUTES, //时间单位
                new ArrayBlockingQueue<>(10000)); //任务队列

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        //分组次
        for (int i = 0; i < INSERT_NUM / batchSize; i++) {
            List<User> userList = //使用synchronized成为线程安全的集合
                    Collections.synchronizedList(new ArrayList<>()); //异步任务列表
            while(true){
                j++;
                User user = new User();
                user.setUserAccount("假用户2");
                user.setUserName("pj123");
                user.setGender(0);
                user.setAvatarUrl("https://s2.loli.net/2024/06/29/xaqI7Riryk3p9to.jpg");
                user.setUserPassword("12345678");
                user.setPhone("110");
                user.setEmail("110@qq.com");
                user.setUserStatus(0);
                user.setUpdateTime(LocalDateTime.now());
                user.setUserRole(0);
                user.setValidCode("110");
                user.setTags("[]");

                userList.add(user);
                if(j % batchSize == 0){
                    break;
                }
            }
            //异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName: " + Thread.currentThread().getName());
                userService.saveBatch(userList, 10000);
            }, executorService); //添加线程池 不然就是默认线程池
            futureList.add(future);
        }

        // 等待所有异步任务完成 join()方法会阻塞当前线程，直到所有CompletableFuture完成。
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

        stopWatch.stop();
        System.out.println(stopWatch.getLastTaskTimeMillis());
    }
}