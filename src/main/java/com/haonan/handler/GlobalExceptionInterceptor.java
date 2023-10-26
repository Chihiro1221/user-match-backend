package com.haonan.handler;

import com.haonan.common.BaseResponse;
import com.haonan.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionInterceptor {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse exceptionHandler(BusinessException e) {
        log.error("发生错误：{} - {}", e.getCode(), e.getMessage());
        return BaseResponse.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public void exception(Throwable e) {
        log.error("发生错误：{} - {}", e.getMessage());
    }
}
