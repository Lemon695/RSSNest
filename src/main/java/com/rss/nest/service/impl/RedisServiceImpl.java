package com.rss.nest.service.impl;

import com.rss.nest.service.IRedisService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:07:23
 * @description:
 */
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {

    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Resource(name = "objRedisTemplate")
    private RedisTemplate<String, Object> objRedisTemplate;

    @Resource(name = "redisTemplate")
    private StringRedisTemplate StringRedisTemplate1;

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate StringRedisTemplate2;

    private ValueOperations valueOps;
    private ListOperations listOps;
    private ValueOperations stringValueOps;

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate<String, String> redisTemplate1;

    @PostConstruct
    private void init() {
        this.valueOps = this.objRedisTemplate.opsForValue();
        this.listOps = this.objRedisTemplate.opsForList();
        this.stringValueOps = this.StringRedisTemplate2.opsForValue();
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return key == null ? null : objRedisTemplate.opsForValue().get(key);
    }

    /**
     * 设定小时-存储redis
     *
     * @param redisKey   KEY值
     * @param redisValue 缓存数据
     * @param hours      小时
     */
    @Override
    public void setValueByHours(String redisKey, String redisValue, int hours) {
        this.redisTemplate1.opsForValue().set(redisKey, redisValue, Duration.ofHours(hours));
    }

    /**
     * 设定天-存储redis
     *
     * @param redisKey   KEY值
     * @param redisValue 缓存数据
     * @param days       天
     */
    @Override
    public void setValueByDays(String redisKey, String redisValue, int days) {
        this.redisTemplate1.opsForValue().set(redisKey, redisValue, Duration.ofDays(days));
    }


}
