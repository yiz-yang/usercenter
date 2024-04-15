package com.rong.usercenterbackend.service;

import com.rong.usercenterbackend.model.doMain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
* @author m
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-03-17 16:10:26
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账号
     * @param userPassword 用户密码
     * @param checkPassword 校验密码
     * @return id 新用户
     */
    long userRegister(String userAccount,String userPassword,String checkPassword );


    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(User user, User loginUser);

    boolean isAdmin(HttpServletRequest request);

    boolean isAdmin(User loginUser);

    long addUser(User user, User loginUser);
}
