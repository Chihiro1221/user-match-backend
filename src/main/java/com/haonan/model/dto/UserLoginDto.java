package com.haonan.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginDto implements Serializable {
    private String username;
    private String password;
}
