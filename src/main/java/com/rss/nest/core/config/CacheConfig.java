package com.rss.nest.core.config;

import lombok.Data;

/**
 * 缓存配置类
 */
@Data
public class CacheConfig {

    /**
     * 缓存时长（秒）
     */
    private Long ttl = 10800L; // 默认3小时

    /**
     * 是否启用缓存
     */
    private Boolean enabled = true;

    /**
     * 缓存key前缀
     */
    private String keyPrefix;

    /**
     * 是否缓存空值
     */
    private Boolean cacheNullValues = false;
}
