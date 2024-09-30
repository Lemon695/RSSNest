package com.rss.nest.common.interceptor;


import com.rss.nest.common.constant.ConstantValue;
import com.rss.nest.common.constant.D6Protocol;
import com.rss.nest.query.IRssCacheDataQueryService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:23:23
 * @description: 前置请求-判断是否是RSS接口,优先读取缓存数据
 */
@Slf4j
@Component
public class RedisCacheInterceptor implements HandlerInterceptor {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private IRssCacheDataQueryService rssCacheDataQueryService;

    /**
     * @param request  请求
     * @param response 响应
     * @param handler  拦截器
     * @throws Exception 异常
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (StringUtils.equalsIgnoreCase(request.getMethod(), ConstantValue.HTTP_OPTIONS)) {
            return true;
        }

        // 判断请求方式，get还是post还是其他。。。
        if (!StringUtils.equalsIgnoreCase(request.getMethod(), ConstantValue.HTTP_GET) && !StringUtils.equalsIgnoreCase(request.getMethod(), ConstantValue.HTTP_POST)) {
            // 非get请求，如果不是graphql请求，放行
            if (!StringUtils.equalsIgnoreCase(request.getRequestURI(), "/graphql")) {
                return true;
            }
        }

        String data = "";
        // 通过缓存做命中，查询redis，redisKey ?  组成：md5（请求的url + 请求参数）
        String rssRedisKey = rssCacheDataQueryService.createRssRedisKey(request);
        if (StringUtils.isNotBlank(rssRedisKey)) {
            request.setAttribute(D6Protocol.RSS_REDIS_KEY, rssRedisKey);
            log.info(String.format("get rssRedisKey---%s", rssRedisKey));
            data = this.redisTemplate.opsForValue().get(rssRedisKey);
        }

        if (StringUtils.isEmpty(data)) {
            // 缓存未命中
            return true;
        }

        log.info("Redis存在RSS数据，key={}", request.getRequestURI());

        /*
          将data数据进行响应
          application/json
          application/xml
          text/plain
          text/xml
         */
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/rss+xml; charset=utf-8");

        // 支持跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Token");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.getWriter().print(data);
        return false;
    }

}
