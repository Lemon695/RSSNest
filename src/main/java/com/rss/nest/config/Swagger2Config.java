package com.rss.nest.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Lemon695
 * @date: 2024/10/1 上午12:02:00
 * @description:
 */
@Configuration
@EnableKnife4j
public class Swagger2Config implements WebMvcConfigurer {


    /**
     * 显示swagger-ui.html文档展示页，还必须注入swagger资源：
     *
     * @param registry 注册类
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 解决静态资源无法访问
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");

        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}