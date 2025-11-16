package com.rss.nest.core.provider;

import com.rss.nest.core.config.SiteConfig;
import com.rss.nest.models.rss.RssChannel;

import java.util.Map;

/**
 * RSS提供者服务接口
 * 所有网站的RSS生成服务都需要实现此接口
 */
public interface RssProviderService {

    /**
     * 生成RSS
     *
     * @param params 请求参数（如分类、页码等）
     * @return RSS Channel对象
     */
    RssChannel generateRss(Map<String, String> params);

    /**
     * 获取网站标识符
     *
     * @return 网站唯一标识
     */
    String getSiteIdentifier();

    /**
     * 获取网站配置
     *
     * @return 网站配置对象
     */
    SiteConfig getSiteConfig();

    /**
     * 验证参数是否有效
     *
     * @param params 请求参数
     * @return 是否有效
     */
    default boolean validateParams(Map<String, String> params) {
        return true;
    }

    /**
     * 获取支持的参数列表
     *
     * @return 参数说明
     */
    default String getSupportedParams() {
        return "无特殊参数";
    }
}
