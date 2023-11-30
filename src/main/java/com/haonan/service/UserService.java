package com.haonan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haonan.model.dto.UserRecommendDto;
import com.haonan.model.dto.UserSearchDto;
import com.haonan.model.dto.UserUpdateDto;
import com.haonan.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haonan.model.vo.UserVO;
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
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    /**
     * 根据条件动态搜索用户
     */
    List<User> search(UserSearchDto userSearchDto);

    /**
     * 更新用户
     *
     * @param userUpdateDto
     * @param request
     */
    void updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request);


    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    boolean isAdmin(User user);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 推荐用户
     * @return
     */
    Page<User> recommendUsers(UserRecommendDto userRecommendDto);

    /**
     * 匹配用户
     * @return
     */
    List<UserVO> matchUsers(Integer num);
}
