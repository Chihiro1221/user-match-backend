package com.haonan.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.haonan.mapper.UserMapper;
import com.haonan.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

@SpringBootTest
public class InsertUsers {
    @Resource
    private UserService userService;

    @Test
    void insertUsers() {
        Integer MAX_NUM = 10000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ArrayList<User> userList = new ArrayList<>();
        for (int i = 0; i < MAX_NUM; i++) {
            User user = new User();
            user.setNickname("测试账号");
            user.setAvatarUrl("https://heart-sky-take-out.oss-cn-beijing.aliyuncs.com/%E9%B1%BC%E8%81%AA%E6%98%8EAI%E7%BB%98%E7%94%BB%20%281%29.jpeg");
            user.setUsername("test");
            user.setPassword("youzhi..");
            user.setGender(0);
            user.setPhone("15933371902");
            user.setEmail("2213595911@qq.com");
            user.setPlanetCode("5");
            user.setTags("['java','c++']");
            user.setIntroduction("这里是测试账号");
            userList.add(user);
        }
        userService.saveBatch(userList, 2000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());
    }

    @Test
    void concurrentInsertUsers() {
        Integer MAX_NUM = 100000;
        StopWatch stopWatch = new StopWatch();
        ArrayList<CompletableFuture<Void>> futureList = new ArrayList<>();
        stopWatch.start();
        // 采用多线程分10个线程，每个线程处理1000条插入数据
        for (int i = 0; i < 10; i++) {
            ArrayList<User> userList = new ArrayList<>();
            for (int j = 0; j < MAX_NUM / 10; j++) {
                User user = new User();
                user.setNickname("测试账号");
                user.setAvatarUrl("https://heart-sky-take-out.oss-cn-beijing.aliyuncs.com/%E9%B1%BC%E8%81%AA%E6%98%8EAI%E7%BB%98%E7%94%BB%20%281%29.jpeg");
                user.setUsername("test");
                user.setPassword("youzhi..");
                user.setGender(0);
                user.setPhone("15933371902");
                user.setEmail("2213595911@qq.com");
                user.setPlanetCode("5");
                Gson gson = new Gson();
                List<String> tags = List.of("java", "c++", "python");
                user.setTags(gson.toJson(tags));
                user.setIntroduction("这里是测试账号");
                userList.add(user);
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                userService.saveBatch(userList, 200);
            });
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeSeconds());
    }
}
