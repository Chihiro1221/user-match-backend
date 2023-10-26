package com.haonan.exception;

public enum ErrorCode {
    CLIENT_ERROR(40000, "客户端错误！"),
    USER_NOT_EXIST_ERROR(40001, "用户不存在！"),
    USER_EXIST_ERROR(40002, "用户已存在！"),
    PLANET_CODE_EXIST_ERROR(40003, "星球编号已存在！"),
    PARAMS_ERROR(42200, "参数错误！"),
    NO_PERMISSION_ERROR(40300, "没有访问权限！"),
    NO_LOGIN_ERROR(40100, "请您登录！"),
    SYSTEM_ERROR(50000, "系统内部异常，请稍后再试！");

    private Integer code;
    private String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
