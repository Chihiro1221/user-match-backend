package com.haonan.model.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateDto extends TeamAddDto implements Serializable {
    /**
     * 队伍名
     */
    @NotBlank(message = "队伍名不能为空")
    @Size(max = 20)
    private String name;

    /**
     * 过期时间
     */
    @Future
    private Date expireTime;
}