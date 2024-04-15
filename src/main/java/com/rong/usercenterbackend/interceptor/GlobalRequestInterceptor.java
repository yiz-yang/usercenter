package com.rong.usercenterbackend.interceptor;

import com.rong.usercenterbackend.common.ErrorCode;

import com.rong.usercenterbackend.exception.BusinessException;

import com.rong.usercenterbackend.model.doMain.User;

import lombok.extern.slf4j.Slf4j;


import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static com.rong.usercenterbackend.contant.UserConstant.ADMIN_ROLE;
import static com.rong.usercenterbackend.contant.UserConstant.USER_LOGIN_STATE;

@Slf4j
public class GlobalRequestInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        if (session == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Object userObj = session.getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        User user = (User) userObj;
        if (user.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH, "不是管理员");
        }
        return true;
    }

    // 记录请求日志
//        String requestUrl = request.getRequestURI();
//        log.info("Request URL:" + requestUrl);

}
