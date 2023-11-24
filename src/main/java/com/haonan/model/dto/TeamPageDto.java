package com.haonan.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamPageDto extends BasePageDto{
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
