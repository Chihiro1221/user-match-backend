package com.haonan.service;

import com.haonan.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author wanghaonan
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-10-22 12:15:50
 */
public interface UserService extends IService<User> {
    /**
     * 用户注册
     *
     * @param username             用户名
     * @param password             密码
     * @param passwordConfirmation 重复密码
     * @param planetCode
     * @return 用户id
     */
    long register(String username, String password, String passwordConfirmation, String planetCode);

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     */
    void login(String username, String password, HttpServletRequest request);

    /**
     * 获取脱敏之后的用户数据
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser) ;

    /**
     * 根据标签查询用户
     *
     * @param tags
     * @return
     */
    List<User> getUsersByTags(List<String> tags);
}
