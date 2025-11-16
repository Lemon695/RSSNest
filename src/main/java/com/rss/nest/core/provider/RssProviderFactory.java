package com.rss.nest.core.provider;

import com.rss.nest.core.exception.UnsupportedSiteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RSS提供者工厂
 * 负责管理和获取各个网站的RSS提供者服务
 */
@Slf4j
@Component
public class RssProviderFactory {

    /**
     * 存储所有Provider的Map
     * key: siteId, value: RssProviderService实例
     */
    private final Map<String, RssProviderService> providers = new ConcurrentHashMap<>();

    /**
     * 构造函数自动注册所有Provider
     * Spring会自动注入所有RssProviderService的实现类
     *
     * @param providerList 所有Provider实现类的列表
     */
    @Autowired
    public RssProviderFactory(List<RssProviderService> providerList) {
        log.info("开始初始化RSS Provider工厂");

        for (RssProviderService provider : providerList) {
            String siteId = provider.getSiteIdentifier();
            providers.put(siteId, provider);
            log.info("注册RSS Provider: {} - {}", siteId, provider.getClass().getSimpleName());
        }

        log.info("RSS Provider工厂初始化完成，共注册 {} 个Provider", providers.size());
    }

    /**
     * 根据网站ID获取对应的Provider
     *
     * @param siteId 网站唯一标识
     * @return RSS提供者服务
     * @throws UnsupportedSiteException 如果网站不支持
     */
    public RssProviderService getProvider(String siteId) {
        RssProviderService provider = providers.get(siteId);
        if (provider == null) {
            log.error("请求了不支持的网站: {}", siteId);
            throw new UnsupportedSiteException(siteId);
        }
        return provider;
    }

    /**
     * 检查是否支持某个网站
     *
     * @param siteId 网站ID
     * @return 是否支持
     */
    public boolean isSupported(String siteId) {
        return providers.containsKey(siteId);
    }

    /**
     * 获取所有支持的网站ID列表
     *
     * @return 网站ID列表
     */
    public List<String> getSupportedSites() {
        return providers.keySet().stream().sorted().toList();
    }

    /**
     * 获取所有Provider的详细信息
     *
     * @return Provider信息列表
     */
    public List<ProviderInfo> getAllProvidersInfo() {
        return providers.values().stream()
                .map(provider -> new ProviderInfo(
                        provider.getSiteIdentifier(),
                        provider.getSiteConfig().getSiteName(),
                        provider.getSiteConfig().getBaseUrl(),
                        provider.getSupportedParams()
                ))
                .sorted((a, b) -> a.getSiteId().compareTo(b.getSiteId()))
                .toList();
    }

    /**
     * Provider信息类
     */
    public static class ProviderInfo {
        private String siteId;
        private String siteName;
        private String baseUrl;
        private String supportedParams;

        public ProviderInfo(String siteId, String siteName, String baseUrl, String supportedParams) {
            this.siteId = siteId;
            this.siteName = siteName;
            this.baseUrl = baseUrl;
            this.supportedParams = supportedParams;
        }

        public String getSiteId() {
            return siteId;
        }

        public String getSiteName() {
            return siteName;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public String getSupportedParams() {
            return supportedParams;
        }
    }
}
