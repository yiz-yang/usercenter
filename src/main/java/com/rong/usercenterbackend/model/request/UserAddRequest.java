package com.rong.usercenterbackend.model.request;



import lombok.Data;

import java.io.Serializable;


/**
 * 用户登录请求体
 */
@Data
public class UserAddRequest implements Serializable {
    private static final long serialVersionUID = -8569875761853398905L;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;



}
