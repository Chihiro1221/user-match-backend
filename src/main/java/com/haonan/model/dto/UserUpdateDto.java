package com.haonan.model.dto;

import com.haonan.exception.ErrorCode;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateDto {
    /**
     * id
     */
    @NotNull
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户头像
     */
    private String avatarUrl;

     /**
     * 密码
     */
    private String password;

    /**
     * 性别 0 男 1 女
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 用户标签
     */
    private String tags;

    /**
     * 用户简介
     */
    private String introduction;
}
