package com.haonan.configuration;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    @Bean
    public RedissonClient redissonClient()
    {
        // 1. 创建redisson配置对象
        Config config = new Config();
        String redisUri = String.format("redis://%s:%s", host, port);
        System.out.println(redisUri);
        config.useSingleServer().setAddress(redisUri).setDatabase(1);
        // 2. 创建Redisson实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
