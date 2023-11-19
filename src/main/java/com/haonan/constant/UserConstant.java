package com.haonan.constant;

/**
 * 用户相关常量
 */
public interface UserConstant {
    /**
     * 加密盐
     */
    final String MD5_SALT = "haonan666";
    /**
     * session域中的key
     */
    final String SESSION_KEY = "USER";
    /**
     * 角色 0 - 普通用户 1 - 管理员
     */
    final Integer ADMIN = 1;
    final Integer USER = 0;
    /**
     * 默认头像地址
     */
    final String DEFAULT_AVATAR = "https://heart-sky-take-out.oss-cn-beijing.aliyuncs.com/%E9%B1%BC%E8%81%AA%E6%98%8EAI%E7%BB%98%E7%94%BB%20%281%29.jpeg";
    /**
     * redis key
     */
    final String REDIS_USER_KEY = "haonan:user:recommend:";
}
