package com.haonan.model.dto;

import lombok.Data;

@Data
public class BasePageDto {
    private Integer pageNum = 1;
    private Integer pageSize = 10;
}
