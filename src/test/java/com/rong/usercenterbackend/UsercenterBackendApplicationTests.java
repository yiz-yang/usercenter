package com.rong.usercenterbackend;

import com.rong.usercenterbackend.common.ErrorCode;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.security.NoSuchAlgorithmException;

@SpringBootTest
class UsercenterBackendApplicationTests {

    @Test
    void testDigest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("gnoy" + "12345678").getBytes());
        System.out.println(newPassword);

    }



    @Test
    void contextLoads() {
        ErrorCode errorCode = ErrorCode.NO_AUTH;
        System.out.println(ErrorCode.NO_AUTH);
        System.out.println(errorCode.getCode());
        System.out.println(errorCode.getMessage());
        System.out.println(errorCode.getDescription());
    }



}
