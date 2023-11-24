package com.haonan.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TeamJoinDto {
    @NotNull
    private Long teamId;
    private String password;
}

