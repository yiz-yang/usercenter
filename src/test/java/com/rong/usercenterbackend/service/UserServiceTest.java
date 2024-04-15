package com.rong.usercenterbackend.service;
import java.util.Date;

import com.rong.usercenterbackend.model.doMain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * 用户服务测试
 */
@SpringBootTest
class UserServiceTest {


    @Resource
    private UserService userService;

    @Test
    void testAddUser(){
        User user = new User();
        user.setUsername("鱼皮");
        user.setUserAccount("yupi");
        user.setAvatarUrl("https://himg.bdimg.com/sys/portraitn/item/public.1.e137c1ac.yS1WqOXfSWEasOYJ2-0pvQ");
        user.setGender(0);
        user.setUserPassword("12345678");
        user.setPhone("13845614");
        user.setEmail("fwafw@qq.com");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "yupi1";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "12345";
        long result = userService.userRegister(userAccount, userPassword, checkPassword );
        Assertions.assertEquals(-1,result);
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword );
        Assertions.assertEquals(-1,result);
        userAccount = "yu";
        userPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword );
        Assertions.assertEquals(-1,result);
        userAccount = "y  ur%#t";
        result = userService.userRegister(userAccount, userPassword, checkPassword );
        Assertions.assertEquals(-1,result);
        userPassword = "12345678";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword );
        Assertions.assertEquals(-1,result);
        userAccount = "yupi";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword );
        Assertions.assertEquals(-1,result);
        userAccount = "dogYupi";
        result = userService.userRegister(userAccount, userPassword, checkPassword );
//        Assertions.assertTrue(result>0);
        Assertions.assertEquals(-1,result);
    }
}