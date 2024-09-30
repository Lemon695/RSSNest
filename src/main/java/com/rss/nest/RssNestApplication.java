package com.rss.nest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:23:23
 * @description:
 */
@Slf4j
@EnableScheduling
@EnableCaching
@EnableAsync
@EnableWebMvc
@SpringBootApplication
public class RssNestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssNestApplication.class, args);
    }

}
