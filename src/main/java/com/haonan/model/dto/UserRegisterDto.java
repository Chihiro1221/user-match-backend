package com.haonan.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterDto implements Serializable {
    private String username;
    private String password;
    private String passwordConfirmation;
    private String planetCode;
}
