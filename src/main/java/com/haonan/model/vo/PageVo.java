package com.haonan.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 统一分页响应对象
 *
 * @param <T>
 */
@Data
public class PageVo<T> {
    List<T> records;
    Integer total;
    Integer size;
    Integer current;
    Integer pages;
}
