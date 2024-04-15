package com.rong.usercenterbackend.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rong.usercenterbackend.common.BaseResponse;
import com.rong.usercenterbackend.common.ErrorCode;
import com.rong.usercenterbackend.common.ResultUtils;
import com.rong.usercenterbackend.exception.BusinessException;
import com.rong.usercenterbackend.model.doMain.User;
import com.rong.usercenterbackend.model.request.UserAddRequest;
import com.rong.usercenterbackend.model.request.UserLoginRequest;
import com.rong.usercenterbackend.model.request.UserRegisterRequest;
import com.rong.usercenterbackend.service.UserService;
import com.rong.usercenterbackend.utils.AliOSSUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


import static com.rong.usercenterbackend.contant.UserConstant.USER_LOGIN_STATE;


/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AliOSSUtils aliOSSUtils;

    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if (userLoginRequest==null){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }


    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        if (request==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){
            queryWrapper.like("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/add")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        User user = new User();
        BeanUtils.copyProperties(userAddRequest, user);
        long userId = userService.addUser(user, loginUser);
        return ResultUtils.success(userId);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验参数是否为空
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request){
        if (!userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }


//    /**
//     * 是否为管理员
//     * @param request
//     * @return
//     */
//    private boolean isAdmin(HttpServletRequest request) {
//        //仅管理员可查询
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user = (User) userObj;
//        return user !=null && user.getUserRole() ==ADMIN_ROLE;
//    }

    @PostMapping("/upload")
    public String upload(@RequestPart(value = "file",required = true) MultipartFile multipartFile){
        if (multipartFile==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //上传图片到阿里云OSS
        String url = null;
        try {
            url = aliOSSUtils.upload(multipartFile);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return url;
    }


}

