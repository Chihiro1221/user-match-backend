package com.haonan.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSearchDto implements Serializable {
    private String nickname;
    /**
     * 样式 java,html,大一
     */
    private String tagNameList;

}
