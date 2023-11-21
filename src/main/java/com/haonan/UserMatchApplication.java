package com.haonan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.haonan.mapper")
@EnableScheduling
public class UserMatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMatchApplication.class, args);
    }

}
