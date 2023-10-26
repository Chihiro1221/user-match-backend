package com.haonan.common;

import com.haonan.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 通用返回对象
 */
@Data
@AllArgsConstructor
public class BaseResponse<T> {
    private Integer code;
    private String message = "";
    private T data;

    public BaseResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BaseResponse(Integer code) {
        this.code = code;
        this.message = message;
    }

    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, "ok", data);
    }

    /**
     * 成功
     *
     * @param <T>
     * @return
     */
    public static <T> BaseResponse<T> success() {
        return new BaseResponse<>(0, "ok", null);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static BaseResponse error(Integer code, String message) {
        return new BaseResponse<>(code, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), message);
    }
}
