package com.haonan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.haonan.constant.UserConstant;
import com.haonan.context.BaseContext;
import com.haonan.exception.BusinessException;
import com.haonan.exception.ErrorCode;
import com.haonan.model.dto.UserRecommendDto;
import com.haonan.model.dto.UserSearchDto;
import com.haonan.model.dto.UserUpdateDto;
import com.haonan.model.entity.User;
import com.haonan.model.vo.UserVO;
import com.haonan.service.UserService;
import com.haonan.mapper.UserMapper;
import com.haonan.utils.AlgorithmUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.marshalling.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wanghaonan
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-10-22 12:15:50
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public long register(String username, String password, String passwordConfirmation, String planetCode) {
        // 每个字段都不能为空
        if (StringUtils.isAnyBlank(username, password, passwordConfirmation, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段不能为空！");
        }
        // 用户名不能低于4位
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不低于4位");
        }
        // 星球编号不超过5位
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号不超过5位");
        }
        // 密码不能低于8位
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不低于8位");
        }
        // 两次密码不一致
        if (!password.equals(passwordConfirmation)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        // 用户名不能有特殊字符
        String regex = ".*\\s+.*";
        Matcher matcher = Pattern.compile(regex).matcher(username);
        if (matcher.matches()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码包含特殊字符");
        }
        // 用户名不能重复
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("username", username);
        long count = this.count(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_EXIST_ERROR);
        }
        // 星球编号不能重复
        userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("planet_code", planetCode);
        count = this.count(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PLANET_CODE_EXIST_ERROR);
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.MD5_SALT + password).getBytes());
        User user = new User();
        user.setUsername(username);
        user.setPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        user.setAvatarUrl(UserConstant.DEFAULT_AVATAR);
        // 保存到数据库
        boolean result = this.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.CLIENT_ERROR, "注册用户失败");
        }
        return user.getId();
    }

    @Override
    public void login(String username, String password, HttpServletRequest request) {
        // 1. 基本校验
        // 每个字段都不能为空
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "字段不能为空！");
        }
        // 用户名不能低于6位
        if (username.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不低于4位");
        }
        // 密码不能低于8位
        if (password.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不低于8位");
        }
        // 2. 对比账号密码
        String encryptPassword = DigestUtils.md5DigestAsHex((UserConstant.MD5_SALT + password).getBytes());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper
                .eq("username", username)
                .eq("password", encryptPassword);
        User user = userMapper.selectOne(userQueryWrapper);
        // 为空表示没有匹配的账号
        if (user == null) {
            log.info("user login failed, Cos username cannot match password");
            throw new BusinessException(ErrorCode.USER_NOT_EXIST_ERROR, "用户不存在或密码错误");
        }
        // 保存登录域信息
        request.getSession().setAttribute(UserConstant.SESSION_KEY, user);
    }

    /**
     * 获取脱敏之后的用户数据
     *
     * @return
     */
    public User getSafetyUser(User originUser) {
        if (originUser == null) return null;
        User user = new User();
        user.setId(originUser.getId());
        user.setNickname(originUser.getNickname());
        user.setAvatarUrl(originUser.getAvatarUrl());
        user.setUsername(originUser.getUsername());
        user.setGender(originUser.getGender());
        user.setPhone(originUser.getPhone());
        user.setEmail(originUser.getEmail());
        user.setStatus(originUser.getStatus());
        user.setCreateTime(originUser.getCreateTime());
        user.setRole(originUser.getRole());
        user.setPlanetCode(originUser.getPlanetCode());
        user.setTags(originUser.getTags());
        user.setIntroduction(originUser.getIntroduction());
        return user;
    }

    /**
     * 根据参数动态搜索用户
     *
     * @param userSearchDto
     * @return
     */
    @Override
    public List<User> search(UserSearchDto userSearchDto) {
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        if (userSearchDto != null) {
            userQueryWrapper.like(StringUtils.isNotBlank(userSearchDto.getNickname()), "nickname", userSearchDto.getNickname());

            if (StringUtils.isNotBlank(userSearchDto.getTagNameList())) {
                // 将标签字符串拆分为数组
                String[] tagNameList = userSearchDto.getTagNameList().split(",");
                for (String tagName : tagNameList) {
                    userQueryWrapper = userQueryWrapper.like("tags", tagName).or();
                }
            }
        }

        List<User> userList = userMapper.selectList(userQueryWrapper);

        return userList.stream().map(user -> {
            user.setPassword(null);
            return user;
        }).collect(Collectors.toList());
    }

    /**
     * 更新用户
     *
     * @param userUpdateDto
     * @param request
     */
    @Override
    public void updateUser(UserUpdateDto userUpdateDto, HttpServletRequest request) {
        Long userId = userUpdateDto.getId();
        User originUser = userMapper.selectById(userId);
        // 要修改的用户不存在
        if (originUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST_ERROR);
        }
        // 如果不是管理员，要修改的用户与当前登录也不一致则报错
        if (!isAdmin(originUser) && !userUpdateDto.getId().equals(getLoginUser(request).getId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION_ERROR);
        }

        User user = new User();
        BeanUtils.copyProperties(userUpdateDto, user);
        userMapper.updateById(user);
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    @Override
    public boolean isAdmin(User user) {
        return user.getRole() == UserConstant.ADMIN;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        User loginUser = (User) request.getSession().getAttribute(UserConstant.SESSION_KEY);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN_ERROR);
        }
        return loginUser;
    }

    /**
     * 推荐用户
     *
     * @return
     */
    @Override
    public Page<User> recommendUsers(UserRecommendDto userRecommendDto) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        User loginUser = BaseContext.getCurrentUser();
        Long userId;
        // 所有未登录的用户id都改为0，这样所有未登录的用户都是使用同一份缓存
        userId = loginUser == null ? 0L : loginUser.getId();

        String REDIS_KEY = UserConstant.REDIS_USER_KEY + userId + ":" + userRecommendDto.getPageNum();
        Page<User> redisUserPage = (Page<User>) valueOperations.get(REDIS_KEY);
        // 有缓存数据则返回缓存数据
        if (redisUserPage != null) return redisUserPage;
        // 查询并将结果添加到缓存中
        Page<User> page = new Page<>(userRecommendDto.getPageNum(), userRecommendDto.getPageSize());
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        Page<User> userPage = userMapper.selectPage(page, userQueryWrapper);
        userPage.setRecords(userPage.getRecords().stream().map(this::getSafetyUser).collect(Collectors.toList()));
        try {
            // 1天之后过期
            valueOperations.set(REDIS_KEY, userPage, 60 * 60 * 24, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("redis设置失败：", e);
        }
        return userPage;
    }

    /**
     * 推荐相似度最匹配的用户
     *
     * @return
     */
    @Override
    public List<UserVO> matchUsers(Integer num) {
        User currentUser = BaseContext.getCurrentUser();
        String tags = currentUser.getTags();
        Gson gson = new Gson();
        // 将当前用户标签转换为list
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 查询出所有用户（不包括标签为空、不包括自己、只查询id、tags字段）
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.select("id", "tags");
        queryWrapper.isNotNull("tags");
        queryWrapper.ne("id", currentUser.getId());
        List<User> users = this.list(queryWrapper);
        List<Pair<Long, Integer>> editDistanceList = new ArrayList<>();
        // 计算出每一个用户与当前登录用户的标签匹配值，存储到编辑距离数据中
        for (User user : users) {
            String userTags = user.getTags();
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            int score = AlgorithmUtils.minDistance(tagList, userTagList);
            editDistanceList.add(new Pair<Long, Integer>(user.getId(), score));
        }
        // 将编辑距离数组根据分数（分数越小表示编辑距离操作步骤越小，表示越相近）从小到大排序，并取出前num个数据
        List<Pair<Long, Integer>> topUserPairList = editDistanceList.stream().sorted(Comparator.comparingInt(Pair::getB)).limit(num).toList();
        // 获取topN用户id
        List<Long> userIdList = topUserPairList.stream().map(pair -> pair.getA()).toList();
        queryWrapper = new QueryWrapper();
        queryWrapper.in("id",userIdList );
        // 根据id查询出详细的用户信息并封装为vo对象返回出去
        Map<Long, List<User>> uesrMap = userMapper.selectList(queryWrapper).stream().collect(Collectors.groupingBy(User::getId));
        ArrayList<UserVO> userVOList = new ArrayList<>();
        for (Pair<Long, Integer> pair : topUserPairList) {
            User user = uesrMap.get(pair.getA()).get(0);
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVOList.add(userVO);
        }
        return userVOList;
    }
}




