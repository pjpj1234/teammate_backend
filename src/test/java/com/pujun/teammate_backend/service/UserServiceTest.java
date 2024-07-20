package com.pujun.teammate_backend.service;

import com.pujun.teammate_backend.entity.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;



@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    void searchUsersByTagBySQL(){
        List<String> tagList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTagsBySQL(tagList);
        Assertions.assertNotNull(userList);
        System.out.println(userList);
    }

    @Test
    void searchUsersByTag(){
        List<String> tagList = Arrays.asList("java", "python");
        List<User> userList = userService.searchUsersByTags(tagList);
        Assertions.assertNotNull(userList);
        System.out.println(userList);
    }

    @Test
    void userSave() {
        User user = new User();
        user.setId(1L);
        user.setUserAccount("pj123");
        user.setUserName("pj123");
        user.setGender(0);
        user.setAvatarUrl("https://www.rockivy.cn/images/avatar.jpg");
        user.setUserPassword("123456");
        user.setPhone("18384831533");
        user.setEmail("1270217800@qq.com");
        user.setUserStatus(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setIsDelete(0);
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegisterREG() {
        String REG_EX = "[`~!@#$%^*()+\n\r|{}\\[\\]<>/？！（）【】‘；：”“’。，、\\\\]";
        if(Pattern.compile(REG_EX).matcher("in*fo").find()){
            System.out.println("fail");
        }
        else
            System.out.println("success");
    }

    @Test
    void userRegisterEncypt(){
        final String SALT = "pujun";
        String encytry = DigestUtils.md5Hex((SALT + "12345").getBytes());
        System.out.println(encytry);
    }

    @Test
    void userRegister() {
        String userAccount = "pujun";
        String userPassword = "12345678";
        String secPassword = "12345678";
        String validCode = "999";
        Long result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);

        userAccount = "";
        result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);

        userAccount = "123";
        result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);

        userPassword = "123444";
        secPassword = "123444";
        result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);

        userPassword = "123444666";
        secPassword = "123444777";
        result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);

        userAccount = "pujun";
        result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);

        userPassword = "12#3(4)44";
        result = userService.userRegister(userAccount, userPassword, secPassword, validCode);
        Assertions.assertEquals(-1,result);
    }

    @Test
    void userLogin() {
    }

    @Test
    void userFind() {
        String userName = "pj123";
        String gender = "1";
//        List<User>userList = userService.userFind(userName, gender);
//        if(userList.isEmpty()){
//            System.out.println("空");
//        }
//        for(User user:userList){
//            System.out.println(user);
//        }
    }
}