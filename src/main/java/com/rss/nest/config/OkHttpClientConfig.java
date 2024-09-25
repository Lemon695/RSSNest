package com.rss.nest.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author Lemon695
 * @date: 2024/9/25 上午9:59:09
 * @description:
 */
@Configuration
public class OkHttpClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 连接池大小和保持时间
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                // 连接超时时间
                .connectTimeout(30, TimeUnit.SECONDS)
                // 读取超时时间
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
