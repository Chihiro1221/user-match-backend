package com.haonan.handler;

import com.haonan.common.BaseResponse;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse validationException(MethodArgumentNotValidException e) {
        // 获取异常信息
        BindingResult exceptions = e.getBindingResult();
        // 先简单抛出参数错误异常
        return BaseResponse.error(ErrorCode.PARAMS_ERROR);
    }

    @ExceptionHandler(Throwable.class)
    public void exception(Throwable e) {
        log.error("发生错误：{} - {}", e.getMessage());
    }
}
