package com.rss.nest.providers.ithome;

import com.rss.nest.core.config.*;
import com.rss.nest.core.provider.AbstractRssProviderService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * IT之家RSS提供者
 * 使用v2.0新架构实现
 */
@Slf4j
@Service
public class IthomeRssProvider extends AbstractRssProviderService {

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
        log.info("IT之家RSS Provider初始化完成");
    }

    @Override
    public String getSiteIdentifier() {
        return "ithome";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        String category = params.getOrDefault("category", "it");

        IthomeCategoryEnum categoryEnum = IthomeCategoryEnum.getByCode(category);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("不支持的分类: " + category +
                "，支持的分类: it, soft, win10, win11, iphone, ipad, android, digi, next");
        }

        return categoryEnum.getUrl();
    }

    @Override
    public boolean validateParams(Map<String, String> params) {
        if (params == null) {
            return true; // 允许空参数，使用默认值
        }

        String category = params.get("category");
        if (category == null) {
            return true; // 允许空，使用默认值
        }

        // 验证分类
        return IthomeCategoryEnum.isValidCode(category);
    }

    @Override
    public String getSupportedParams() {
        return "category: 分类（it-IT资讯, soft-软件之家, win10-Win10之家, win11-Win11之家, " +
               "iphone-iPhone之家, ipad-iPad之家, android-Android之家, digi-数码之家, next-下一代），默认: it";
    }

    @Override
    protected String getRssTitle(Map<String, String> params) {
        String category = params.getOrDefault("category", "it");
        IthomeCategoryEnum categoryEnum = IthomeCategoryEnum.getByCode(category);

        if (categoryEnum != null) {
            return "IT之家 - " + categoryEnum.getName();
        }
        return "IT之家 - IT资讯";
    }

    @Override
    protected String getRssDescription(Map<String, String> params) {
        String category = params.getOrDefault("category", "it");
        IthomeCategoryEnum categoryEnum = IthomeCategoryEnum.getByCode(category);

        if (categoryEnum != null) {
            return "IT之家" + categoryEnum.getName() + "频道最新资讯";
        }
        return "IT之家最新科技资讯";
    }

    /**
     * 构建网站配置
     */
    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("ithome");
        config.setSiteName("IT之家");
        config.setBaseUrl("https://www.ithome.com");
        config.setEnabled(true);

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        config.setHeaders(headers);

        // 设置HTML解析配置
        config.setParseConfig(buildParseConfig());

        // 设置RSS配置
        config.setRssConfig(buildRssConfig());

        // 设置缓存配置
        config.setCacheConfig(buildCacheConfig());

        return config;
    }

    /**
     * 构建解析配置
     */
    private ParseConfig buildParseConfig() {
        ParseConfig parseConfig = new ParseConfig();

        // 列表容器选择器 - IT之家的新闻列表
        parseConfig.setListSelector("#list > div.fl > ul > li");

        // 标题选择器
        parseConfig.setTitleSelector("div > h2 > a");

        // 链接选择器
        parseConfig.setLinkSelector("div > h2 > a");
        parseConfig.setLinkAttribute("href");

        // 内容选择器 - IT之家列表页的摘要
        parseConfig.setContentSelector("div > p.desc");
        parseConfig.setContentMode("text");

        // 日期选择器 - IT之家的发布时间
        parseConfig.setDateSelector("div > p.desc > span:first-child");
        parseConfig.setDateFormat("yyyy/M/d HH:mm:ss");

        // 图片选择器 - IT之家的新闻图片
        parseConfig.setImageSelector("a > img");
        parseConfig.setImageAttribute("src");

        // URL配置 - IT之家的链接已经是绝对路径
        parseConfig.setNeedFullUrl(false);

        return parseConfig;
    }

    /**
     * 构建RSS配置
     */
    private RssConfig buildRssConfig() {
        RssConfig rssConfig = new RssConfig();
        rssConfig.setTitle("IT之家RSS订阅");
        rssConfig.setDescription("IT之家 - 快速全面客观的科技新闻");
        rssConfig.setLink("https://www.ithome.com");
        rssConfig.setLanguage("zh-CN");
        rssConfig.setGenerator("RSSNest");
        rssConfig.setItemsPerPage(30);
        rssConfig.setIncludeContent(true);
        rssConfig.setIncludeImage(true);
        return rssConfig;
    }

    /**
     * 构建缓存配置
     */
    private CacheConfig buildCacheConfig() {
        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setEnabled(true);
        cacheConfig.setTtl(1800L); // 30分钟缓存
        cacheConfig.setCacheNullValues(false);
        cacheConfig.setKeyPrefix("ithome");
        return cacheConfig;
    }
}
