package com.rss.nest.core.cache;

import com.rss.nest.models.rss.RssChannel;

import java.util.Map;
import java.util.function.Supplier;

/**
 * RSS缓存服务接口
 */
public interface RssCacheService {

    /**
     * 获取缓存的RSS
     *
     * @param siteId 网站ID
     * @param params 参数
     * @return RSS Channel，不存在返回null
     */
    String get(String siteId, Map<String, String> params);

    /**
     * 设置RSS缓存
     *
     * @param siteId    网站ID
     * @param params    参数
     * @param rssXml    RSS XML内容
     * @param ttlSeconds 缓存时长（秒）
     */
    void set(String siteId, Map<String, String> params, String rssXml, Long ttlSeconds);

    /**
     * 删除缓存
     *
     * @param siteId 网站ID
     * @param params 参数
     */
    void delete(String siteId, Map<String, String> params);

    /**
     * 清空某个网站的所有缓存
     *
     * @param siteId 网站ID
     */
    void clearSite(String siteId);

    /**
     * 获取或设置缓存（防击穿）
     * 如果缓存不存在，执行supplier获取数据并缓存
     *
     * @param siteId     网站ID
     * @param params     参数
     * @param ttlSeconds 缓存时长（秒）
     * @param supplier   数据提供者
     * @return RSS XML内容
     */
    String getOrSet(String siteId, Map<String, String> params, Long ttlSeconds, Supplier<String> supplier);
}
