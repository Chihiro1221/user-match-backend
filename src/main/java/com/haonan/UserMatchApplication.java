package com.haonan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.haonan.mapper")
@EnableScheduling
@EnableTransactionManagement
public class UserMatchApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserMatchApplication.class, args);
    }

}
