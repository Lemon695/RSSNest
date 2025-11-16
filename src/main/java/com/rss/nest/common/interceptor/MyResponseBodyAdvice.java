package com.rss.nest.common.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.rometools.rome.feed.rss.Channel;
import com.rss.nest.query.IRssCacheDataQueryService;
import com.rss.nest.utils.LogUtil;
import com.rss.nest.utils.rss.RssUtil;
import com.rss.nest.utils.rss.check.RssChannelCheckUtil;
import com.rss.nest.utils.spring.SpringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Duration;
import java.util.Objects;

/**
 * @author Lemon695
 * @description:
 * @date: 2021/8/25 下午5:11
 */
@Slf4j
@ControllerAdvice
public class MyResponseBodyAdvice implements ResponseBodyAdvice {

    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private IRssCacheDataQueryService rssCacheDataQueryService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return returnType.hasMethodAnnotation(GetMapping.class);
    }

    /**
     * 返回前处理Response
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            String redisValue = "";
            if (body instanceof String) {
                redisValue = (String) body;
            } else if (body instanceof Channel) {
                //RSS输出格式，转为XML，存储Redis
                Channel channel = (Channel) body;

                if (RssChannelCheckUtil.checkChannelHasItems(channel)) {
                    redisValue = RssUtil.rssChannelOutPutXmlV2(channel);
                }
            } else {
                redisValue = mapper.writeValueAsString(body);
            }

            if (StringUtils.isNotBlank(redisValue) && com.rss.nest.utils.str.StringUtils.isXML(redisValue)) {
                //TODO 缓存3小时
                int rssCacheHour = 3;

                String rssRedisKey = rssCacheDataQueryService.createRssRedisKey(((ServletServerHttpRequest) request).getServletRequest());
                //缓存3小时
                log.info(String.format("set rssRedisKey---%s,cacheTime---%s", rssRedisKey, rssCacheHour));
                this.redisTemplate.opsForValue().set(rssRedisKey, Objects.requireNonNull(redisValue), Duration.ofHours(rssCacheHour));
            }

        } catch (Exception e) {
            log.error(LogUtil.getTagInfo("beforeBodyWrite", String.format("exception=%s", e.toString())));
        }
        return body;
    }
}
