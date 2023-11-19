package com.haonan.service;

import com.haonan.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;
    @Test
    void test()
    {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("str", "hello world!");
        valueOperations.set("integer", 12);
        valueOperations.set("double", 2.25);
        User user = new User();
        user.setId(1L);
        user.setNickname("浩楠");
        valueOperations.set("user11", user);

        Object str = valueOperations.get("str");
        Assertions.assertEquals(str, "hello world!");

        str = valueOperations.get("integer");
        Assertions.assertEquals((Integer) str, 12);

        str = valueOperations.get("double");
        Assertions.assertEquals((Double) str, 2.25);

        System.out.println(valueOperations.get("user11"));
    }
}
