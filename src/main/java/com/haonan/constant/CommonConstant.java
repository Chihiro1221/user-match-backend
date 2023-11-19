package com.haonan.constant;

/**
 * 用户相关常量
 */
public interface CommonConstant {
    /**
     * Redisson分布式锁
     */
    final String REDIS_LOCK_KEY = "haonan:schedule:preLoadUser";
}
