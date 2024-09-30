package com.rss.nest.config;

import com.rss.nest.common.interceptor.CommonInterceptor;
import com.rss.nest.common.interceptor.RedisCacheInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author Lemon695
 * @description:
 * @date: 2024/8/25 下午4:52
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private RedisCacheInterceptor redisCacheInterceptor;
    @Resource
    private CommonInterceptor commonInterceptor;

    /**
     * 【注册拦截器】
     * 对接口进行拦截，Redis缓存
     * 1、rss
     *
     * @param registry 端口
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.commonInterceptor).addPathPatterns("/**");
        registry.addInterceptor(this.redisCacheInterceptor).addPathPatterns("/rss/**");
        // 不拦截的uri
        final String[] commonExclude = {"/error", "/files/**"};
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 所有接口
        registry.addMapping("/**")
                // 是否发送 Cookie
                .allowCredentials(true)
                // 支持域
                .allowedOriginPatterns("*")
                // 支持方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
