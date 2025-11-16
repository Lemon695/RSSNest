package com.rss.nest.core.config;

import lombok.Data;
import java.util.Map;

/**
 * 网站配置类
 * 包含网站的基本信息、解析配置、RSS配置等
 */
@Data
public class SiteConfig {

    /**
     * 网站唯一标识符
     */
    private String siteId;

    /**
     * 网站名称
     */
    private String siteName;

    /**
     * 网站基础URL
     */
    private String baseUrl;

    /**
     * HTTP请求头配置
     */
    private Map<String, String> headers;

    /**
     * HTML解析配置
     */
    private ParseConfig parseConfig;

    /**
     * RSS配置
     */
    private RssConfig rssConfig;

    /**
     * 缓存配置
     */
    private CacheConfig cacheConfig;

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 扩展配置（用于特殊网站的自定义配置）
     */
    private Map<String, Object> extraConfig;
}
