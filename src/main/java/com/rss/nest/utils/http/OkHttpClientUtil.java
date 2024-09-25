package com.rss.nest.utils.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Lemon695
 * @date: 2024/9/25 上午9:59:09
 * @description:
 */
@Slf4j
@Component
public class OkHttpClientUtil {

    /**
     * Json格式
     */
    private static final String DEFAULT_MEDIA_TYPE = "application/json; charset=utf-8";

    /**
     * OkHttpClient请求方法
     */
    private final OkHttpClient okHttpClient;

    public OkHttpClientUtil(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * 发送带有自定义Header的GET请求
     *
     * @param url     请求路径
     * @param headers 请求头
     * @return 响应内容
     */
    public String doGet(String url, Headers headers) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .headers(headers)
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected HTTP response: " + response);
                }
                ResponseBody body = response.body();
                if (body != null) {
                    return body.string();
                } else {
                    throw new RuntimeException("Response body is null");
                }
            }
        } catch (IOException ex) {
            log.error("An error occurred while executing GET request", ex);
            return "";
        }
    }


}
