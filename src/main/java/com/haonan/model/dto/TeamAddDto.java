package com.haonan.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.haonan.enums.TeamStatusEnum;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamAddDto implements Serializable {

    /**
     * 队伍名
     */
    @NotBlank(message = "队伍名不能为空")
    @NotNull
    @Size(max = 20)
    private String name;

    /**
     * 队伍描述
     */
    @Size(max = 512)
    private String description;

    /**
     * 队伍密码
     */
    @Size(max = 18)
    private String password;

    /**
     * 最大人数
     */
    @Max(10)
    @Min(2)
    private Integer maxNum;

    /**
     * 状态 0-公开 1-私有 2-加密
     */
    private Integer status;

    /**
     * 过期时间
     */
    @Future
    @NotNull
    private Date expireTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}