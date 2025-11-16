package com.rss.nest.core.cache;

import com.rss.nest.core.provider.RssProviderFactory;
import com.rss.nest.core.provider.RssProviderService;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.utils.rss.RssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 带缓存的RSS服务
 * 统一处理RSS生成和缓存逻辑
 */
@Slf4j
@Service
public class CacheableRssService {

    @Autowired
    private RssProviderFactory providerFactory;

    @Autowired
    private RssCacheService cacheService;

    /**
     * 生成RSS（带缓存）
     *
     * @param siteId 网站ID
     * @param params 请求参数
     * @return RSS XML字符串
     */
    public String generateRssWithCache(String siteId, Map<String, String> params) {
        log.info("生成RSS, siteId: {}, params: {}", siteId, params);

        // 获取Provider
        RssProviderService provider = providerFactory.getProvider(siteId);

        // 获取缓存配置
        Long ttl = null;
        if (provider.getSiteConfig().getCacheConfig() != null) {
            if (!provider.getSiteConfig().getCacheConfig().getEnabled()) {
                // 缓存未启用，直接生成
                log.debug("缓存未启用, siteId: {}", siteId);
                return generateRssDirectly(provider, params);
            }
            ttl = provider.getSiteConfig().getCacheConfig().getTtl();
        }

        // 使用缓存
        final Long finalTtl = ttl;
        return cacheService.getOrSet(siteId, params, ttl, () -> {
            log.info("缓存未命中，生成新数据, siteId: {}", siteId);
            return generateRssDirectly(provider, params);
        });
    }

    /**
     * 直接生成RSS（不使用缓存）
     */
    private String generateRssDirectly(RssProviderService provider, Map<String, String> params) {
        RssChannel rssChannel = provider.generateRss(params);
        return RssUtil.rssChannelOutPutXml(rssChannel);
    }

    /**
     * 清除缓存
     *
     * @param siteId 网站ID
     * @param params 请求参数
     */
    public void clearCache(String siteId, Map<String, String> params) {
        cacheService.delete(siteId, params);
    }

    /**
     * 清空某个网站的所有缓存
     *
     * @param siteId 网站ID
     */
    public void clearSiteCache(String siteId) {
        cacheService.clearSite(siteId);
    }
}
