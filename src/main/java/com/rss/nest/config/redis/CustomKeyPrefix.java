package com.rss.nest.config.redis;

import cn.hutool.core.lang.Assert;
import org.springframework.data.redis.cache.CacheKeyPrefix;

/**
 * @author Lemon695
 * @description: 替换SpringCache的双冒号为单冒号
 * @date: 2023/9/16 2:19 PM
 */
public interface CustomKeyPrefix extends CacheKeyPrefix {

    String SEPARATOR = ":";

    @Override
    String compute(String cacheName);

    static CustomKeyPrefix simple() {
        return (name) -> name + SEPARATOR;
    }

    static CustomKeyPrefix prefixed(String prefix) {
        Assert.notNull(prefix, "Prefix must not be null!");
        return (name) -> prefix + name + SEPARATOR;
    }

}
