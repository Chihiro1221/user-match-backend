package com.haonan.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserVO implements Serializable {
    /**
     * id
     */
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
     * 用户名
     */
    private String username;

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
     * 状态 状态 0 正常 1封号
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 角色 0 - 普通用户 1 - 管理员
     */
    private Integer role;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}