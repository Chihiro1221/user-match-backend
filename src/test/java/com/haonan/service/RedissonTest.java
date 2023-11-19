package com.haonan.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haonan.constant.CommonConstant;
import com.haonan.constant.UserConstant;
import com.haonan.mapper.UserMapper;
import com.haonan.model.entity.User;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class RedissonTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    @Test
    void test(){
        RList<Object> list = redissonClient.getList("redisson-list");
        list.add("test data");

        RMap<String, String> map = redissonClient.getMap("redisson-map");
        map.put("test", "hello world");
    }

    @Test
    void testLock(){
        RLock lock = redissonClient.getLock(CommonConstant.REDIS_LOCK_KEY);
        try {
            // 只有抢到锁之后才去执行，并且没有抢到的则不再继续抢
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                log.info("locked by thread: " + Thread.currentThread().getId());
                ValueOperations valueOperations = redisTemplate.opsForValue();
                Thread.sleep(60000 * 5);
                // 预加载活跃用户的第一页
                List<Long> userIdList = List.of(1L);
                for (Long userId : userIdList) {
                    String REDIS_KEY = UserConstant.REDIS_USER_KEY + userId + ":" + 1;
                    // 查询并将结果添加到缓存中
                    Page<User> page = new Page<>(1, 10);
                    QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userMapper.selectPage(page, userQueryWrapper);
                    userPage.setRecords(userPage.getRecords().stream().map(userService::getSafetyUser).collect(Collectors.toList()));
                    try {
                        // 1天之后过期
                        valueOperations.set(REDIS_KEY, userPage, 60 * 60 * 24, TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.error("redis设置失败：", e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.info("PreLoadRecommendUser Redisson lock error", e);
        } finally {
            // 只释放自己设置的锁
            if (lock.isHeldByCurrentThread()) {
                log.info("unlocked by thread: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }

    }
}
