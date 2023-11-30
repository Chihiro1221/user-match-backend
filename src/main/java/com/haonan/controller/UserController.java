package com.haonan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haonan.common.BaseResponse;
import com.haonan.constant.UserConstant;
import com.haonan.context.BaseContext;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.model.dto.*;
import com.haonan.model.entity.User;
import com.haonan.model.vo.UserVO;
import com.haonan.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
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
        request.getSession().removeAttribute(UserConstant.SESSION_KEY);
        BaseContext.removeCurrentUser();
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
        User user = userService.getById(BaseContext.getCurrentUser().getId());
        User safetyUser = userService.getSafetyUser(user);
        return BaseResponse.success(safetyUser);
    }


    /**
     * 推荐用户
     *
     * @param userRecommendDto
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page> recommendUsers(UserRecommendDto userRecommendDto) {
        Page<User> userPage = userService.recommendUsers(userRecommendDto);
        return BaseResponse.success(userPage);
    }

    /**
     * 推荐相似度（根据标签）最匹配的用户
     *
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<UserVO>> matchUsers(Integer num) {
        List<UserVO> userVOList = userService.matchUsers(num);
        return BaseResponse.success(userVOList);
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
//        User loginUser = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY);
//        if (loginUser == null) {
//            throw new BusinessException(ErrorCode.NO_LOGIN_ERROR);
//        }
//        if (!loginUser.getRole().equals(UserConstant.ADMIN)) {
//            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
//        }

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

    /**
     * 更新用户信息
     *
     * @return
     */
    @PutMapping("/update")
    public BaseResponse updateUser(@RequestBody @Valid UserUpdateDto userUpdateDto, HttpServletRequest request) {
        if (userUpdateDto == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        userService.updateUser(userUpdateDto, request);
        return BaseResponse.success();
    }
}

