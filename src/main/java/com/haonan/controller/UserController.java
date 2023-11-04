package com.haonan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haonan.common.BaseResponse;
import com.haonan.constant.UserConstant;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.model.dto.UserLoginDto;
import com.haonan.model.dto.UserRegisterDto;
import com.haonan.model.dto.UserSearchDto;
import com.haonan.model.entity.User;
import com.haonan.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterDto
     * @return
     */
    @PostMapping("/register")
    public BaseResponse register(@RequestBody UserRegisterDto userRegisterDto) {
        long userId = userService.register(userRegisterDto.getUsername(), userRegisterDto.getPassword(), userRegisterDto.getPasswordConfirmation(), userRegisterDto.getPlanetCode());
        return BaseResponse.success(userId);
    }

    /**
     * 用户登录
     *
     * @param userLoginDto
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse login(@RequestBody UserLoginDto userLoginDto, HttpServletRequest request) {
        userService.login(userLoginDto.getUsername(), userLoginDto.getPassword(), request);
        return BaseResponse.success();
    }

    /**
     * 用户登出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse logout(HttpServletRequest request) {
        request.removeAttribute(UserConstant.SESSION_KEY);
        return BaseResponse.success();
    }

    /**
     * 获取当前登录用户信息
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User currentUser = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST_ERROR);
        }
        // Todo: 检验用户是否合法
        User user = userService.getById(currentUser.getId());
        User safetyUser = userService.getSafetyUser(user);
        return BaseResponse.success(safetyUser);
    }

    /**
     * 搜索用户
     *
     * @param request
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> search(UserSearchDto userSearchDto, HttpServletRequest request) {
        // Todo: 封装成一个拦截器
        User loginUser = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN_ERROR);
        }
        if (!loginUser.getRole().equals(UserConstant.ADMIN)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
        }

        List<User> userList = userService.search(userSearchDto);
        return BaseResponse.success(userList);
    }

    /**
     * 删除用户
     *
     * @param id
     * @param request
     * @return
     */
    @DeleteMapping("/{id}")
    public BaseResponse deleteById(@PathVariable Long id, HttpServletRequest request) {
        // Todo: 封装成一个拦截器
        User loginUser = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN_ERROR);
        }
        if (loginUser.getRole() != UserConstant.ADMIN) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
        }
        boolean result = userService.removeById(id);
        if (result != true) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "删除用户失败！");
        }
        return BaseResponse.success();
    }
}
