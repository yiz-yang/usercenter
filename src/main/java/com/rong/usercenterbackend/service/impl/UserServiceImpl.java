package com.rong.usercenterbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rong.usercenterbackend.common.ErrorCode;
import com.rong.usercenterbackend.contant.UserConstant;
import com.rong.usercenterbackend.exception.BusinessException;
import com.rong.usercenterbackend.model.doMain.User;
import com.rong.usercenterbackend.service.UserService;
import com.rong.usercenterbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rong.usercenterbackend.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
* @author m
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-03-17 16:10:26
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "gnoy";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        if (userAccount.length()<4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户账号过短");
        }
        if (userPassword.length()<8 || checkPassword.length()<8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户密码过短");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        //校验密码和密码是否相同
        if (!userPassword.equals(checkPassword)){
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if (count>0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
        }
        // 2.对密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            return -1;
        }
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1.校验
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return null;
        }
        if (userAccount.length()<4){
            return null;
        }
        if (userPassword.length()<8){
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = this.getOne(queryWrapper);
        // 用户不存在
        if (user==null){
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3.脱敏
        User safetyUser = getSafetyUser(user);
        // 4.记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser){
        if (originUser==null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request){
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    @Override
    public int updateUser(User user, User loginUser) {
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 补充校验，如果用户没有传任何要更新的值，就直接报错，不用执行 update 语句
        // 如果是管理员，允许更新任意用户
        // 如果不是管理员，只允许更新当前（自己的）信息
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    /**
     * 是否为管理员
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addUser(User user, User loginUser) {
        // 1. 请求参数是否为空？
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 2. 是否登录，未登录不允许创建
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!this.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        // 3. 校验信息
        if (StringUtils.isAnyBlank(user.getUserPassword(),user.getUserAccount())) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "账号和密码不能为空");
        }
        // 8. 插入用户信息到用户表
        User newUser = new User();
        newUser.setUserAccount(user.getUserAccount());
        newUser.setUserPassword(user.getUserPassword());
        boolean result = this.save(newUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "创建用户失败");
        }
        return newUser.getId();
    }

}




