package com.haonan.service;

import com.haonan.mapper.UserMapper;
import com.haonan.model.entity.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setNickname("haonan");
        user.setAvatarUrl("https://thirdwx.qlogo.cn/mmopen/vi_32/yxmEUFibNVe6p4loZtPHhicL9u6Rru6rfLVXZMl2FN9t6WFNyfa3dLDjKUMUthJGwJ4oT0AKImBABMyicbnRhJvWw/132");
        user.setUsername("haonan");
        user.setPassword("youzhi..");
        user.setGender(0);
        user.setPhone("15933371902");
        user.setEmail("2213595911@qq.com");
        int insert = userMapper.insert(user);
        Assertions.assertTrue(insert > 0);
        System.out.println(user.getId());
    }

    @Test
    void register() {
        String username = " ";
        String password = "youzhi..";
        String passwordConfirmation = "youzhi..";
        String planetCode = "12345";
        long result = userService.register(username, password, passwordConfirmation,planetCode );
        Assertions.assertEquals(-1, result);

        username = "hn";
        result = userService.register(username, password, passwordConfirmation,planetCode );
        Assertions.assertEquals(-1, result);

        username = "haonan123";
        password = "123";
        result = userService.register(username, password, passwordConfirmation, planetCode);
        Assertions.assertEquals(-1, result);

        password = "youzhi..";
        passwordConfirmation = "youzhi...";
        result = userService.register(username, password, passwordConfirmation, planetCode);
        Assertions.assertEquals(-1, result);

        passwordConfirmation = "youzhi..";
        username = "  123 123123";
        result = userService.register(username, password, passwordConfirmation, planetCode);
        Assertions.assertEquals(-1, result);

        username = "haonan";
        result = userService.register(username, password, passwordConfirmation, planetCode);
        Assertions.assertEquals(-1, result);

        username = "haonan2";
        result = userService.register(username, password, passwordConfirmation, planetCode);
        Assertions.assertNotEquals(-1, result);
        System.out.println("result = " + result);
    }

    @Test
    void login() {
//        int result = userService.login("haonan2", "youzhi..", getcon);
//        System.out.println("result = " + result);
    }
}