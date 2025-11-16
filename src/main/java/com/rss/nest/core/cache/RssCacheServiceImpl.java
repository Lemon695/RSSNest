package com.rss.nest.core.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * RSS缓存服务实现（Redis）
 */
@Slf4j
@Service
public class RssCacheServiceImpl implements RssCacheService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 缓存key前缀
     */
    private static final String CACHE_PREFIX = "rssNest:cache:";

    /**
     * 分布式锁前缀
     */
    private static final String LOCK_PREFIX = "rssNest:lock:";

    /**
     * 锁超时时间（秒）
     */
    private static final long LOCK_TIMEOUT = 30L;

    @Override
    public String get(String siteId, Map<String, String> params) {
        String key = buildCacheKey(siteId, params);
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("获取缓存失败, key: {}", key, e);
            return null;
        }
    }

    @Override
    public void set(String siteId, Map<String, String> params, String rssXml, Long ttlSeconds) {
        String key = buildCacheKey(siteId, params);
        try {
            if (ttlSeconds != null && ttlSeconds > 0) {
                redisTemplate.opsForValue().set(key, rssXml, Duration.ofSeconds(ttlSeconds));
            } else {
                // 默认3小时
                redisTemplate.opsForValue().set(key, rssXml, Duration.ofHours(3));
            }
            log.debug("设置缓存成功, key: {}, ttl: {}秒", key, ttlSeconds);
        } catch (Exception e) {
            log.error("设置缓存失败, key: {}", key, e);
        }
    }

    @Override
    public void delete(String siteId, Map<String, String> params) {
        String key = buildCacheKey(siteId, params);
        try {
            redisTemplate.delete(key);
            log.debug("删除缓存成功, key: {}", key);
        } catch (Exception e) {
            log.error("删除缓存失败, key: {}", key, e);
        }
    }

    @Override
    public void clearSite(String siteId) {
        try {
            String pattern = CACHE_PREFIX + siteId + ":*";
            var keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("清空网站缓存成功, siteId: {}, 数量: {}", siteId, keys.size());
            }
        } catch (Exception e) {
            log.error("清空网站缓存失败, siteId: {}", siteId, e);
        }
    }

    @Override
    public String getOrSet(String siteId, Map<String, String> params, Long ttlSeconds, Supplier<String> supplier) {
        String cacheKey = buildCacheKey(siteId, params);

        // 1. 尝试从缓存获取
        String cached = get(siteId, params);
        if (cached != null) {
            log.debug("缓存命中, key: {}", cacheKey);
            return cached;
        }

        // 2. 缓存未命中，使用分布式锁防止缓存击穿
        String lockKey = buildLockKey(siteId, params);
        boolean locked = false;

        try {
            // 尝试获取锁
            locked = tryLock(lockKey);

            if (locked) {
                // 获取锁成功，双重检查缓存
                cached = get(siteId, params);
                if (cached != null) {
                    log.debug("双重检查缓存命中, key: {}", cacheKey);
                    return cached;
                }

                // 执行数据获取
                log.debug("执行数据获取, key: {}", cacheKey);
                String data = supplier.get();

                // 设置缓存
                if (data != null) {
                    set(siteId, params, data, ttlSeconds);
                }

                return data;

            } else {
                // 未获取到锁，等待并重试获取缓存
                log.debug("未获取到锁，等待重试, key: {}", cacheKey);
                Thread.sleep(100);

                cached = get(siteId, params);
                if (cached != null) {
                    return cached;
                }

                // 如果还是获取不到，直接执行（降级策略）
                log.warn("缓存获取失败，直接执行, key: {}", cacheKey);
                return supplier.get();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待缓存时被中断, key: {}", cacheKey, e);
            return supplier.get();
        } catch (Exception e) {
            log.error("获取或设置缓存失败, key: {}", cacheKey, e);
            return supplier.get();
        } finally {
            if (locked) {
                releaseLock(lockKey);
            }
        }
    }

    /**
     * 构建缓存Key
     * 格式: rssNest:cache:{siteId}:{paramsHash}
     */
    private String buildCacheKey(String siteId, Map<String, String> params) {
        String paramsStr = buildParamsString(params);
        String hash = DigestUtils.md5DigestAsHex(paramsStr.getBytes(StandardCharsets.UTF_8));
        return CACHE_PREFIX + siteId + ":" + hash;
    }

    /**
     * 构建锁Key
     */
    private String buildLockKey(String siteId, Map<String, String> params) {
        String paramsStr = buildParamsString(params);
        String hash = DigestUtils.md5DigestAsHex(paramsStr.getBytes(StandardCharsets.UTF_8));
        return LOCK_PREFIX + siteId + ":" + hash;
    }

    /**
     * 构建参数字符串
     */
    private String buildParamsString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b)
                .orElse("");
    }

    /**
     * 尝试获取锁
     */
    private boolean tryLock(String lockKey) {
        try {
            Boolean result = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOCK_TIMEOUT, TimeUnit.SECONDS);
            return result != null && result;
        } catch (Exception e) {
            log.error("获取锁失败, lockKey: {}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放锁
     */
    private void releaseLock(String lockKey) {
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception e) {
            log.error("释放锁失败, lockKey: {}", lockKey, e);
        }
    }
}
