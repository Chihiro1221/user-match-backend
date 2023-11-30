package com.haonan.model.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class TeamVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 队伍名
     */
    private String name;

    /**
     * 队伍描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 状态 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 创建人
     */
    private UserVO createUser;

    /**
     * 已加入队伍的用户id
     */
    private List<Long> users;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}