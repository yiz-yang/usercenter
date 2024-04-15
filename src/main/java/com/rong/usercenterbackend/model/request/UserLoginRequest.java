package com.rong.usercenterbackend.model.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -8569875761853398905L;


    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

}
