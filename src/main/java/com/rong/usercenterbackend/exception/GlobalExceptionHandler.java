package com.rong.usercenterbackend.exception;


import com.rong.usercenterbackend.common.BaseResponse;
import com.rong.usercenterbackend.common.ErrorCode;
import com.rong.usercenterbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e){
        log.error("BusinessException: "+e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e){
        log.error("RuntimeException: "+e.getMessage());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage());
    }

}
