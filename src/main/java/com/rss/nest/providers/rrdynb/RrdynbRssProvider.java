package com.rss.nest.providers.rrdynb;

import com.rss.nest.core.config.*;
import com.rss.nest.core.provider.AbstractRssProviderService;
import com.rss.nest.function.rrdynb.enums.RrdynbCategoryEnum;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 人人影视网RSS提供者
 * 使用新架构重构的实现
 */
@Slf4j
@Service
public class RrdynbRssProvider extends AbstractRssProviderService {

    @PostConstruct
    public void init() {
        // 初始化配置
        this.siteConfig = buildSiteConfig();
        log.info("人人影视网RSS Provider初始化完成");
    }

    @Override
    public String getSiteIdentifier() {
        return "rrdynb";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        String category = params.getOrDefault("category", "movie");

        // 验证分类是否有效
        RrdynbCategoryEnum categoryEnum = RrdynbCategoryEnum.getEnumDataByDataType(category);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("不支持的分类: " + category + "，支持的分类: movie, dianshiju, zongyi, dongman");
        }

        return String.format("%s/%s/", siteConfig.getBaseUrl(), category);
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
        return RrdynbCategoryEnum.getEnumDataByDataType(category) != null;
    }

    @Override
    public String getSupportedParams() {
        return "category: 分类（movie-电影, dianshiju-电视剧, zongyi-综艺, dongman-动漫），默认: movie";
    }

    @Override
    protected String getRssTitle(Map<String, String> params) {
        String category = params.getOrDefault("category", "movie");
        RrdynbCategoryEnum categoryEnum = RrdynbCategoryEnum.getEnumDataByDataType(category);

        if (categoryEnum != null) {
            return "人人影视网 - " + categoryEnum.getName();
        }
        return "人人影视网 - 最新影视";
    }

    @Override
    protected String getRssDescription(Map<String, String> params) {
        String category = params.getOrDefault("category", "movie");
        RrdynbCategoryEnum categoryEnum = RrdynbCategoryEnum.getEnumDataByDataType(category);

        if (categoryEnum != null) {
            return "人人影视网最新" + categoryEnum.getName() + "更新";
        }
        return "人人影视网最新内容更新";
    }

    /**
     * 构建网站配置
     */
    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("rrdynb");
        config.setSiteName("人人影视网");
        config.setBaseUrl("https://www.rrdynb.com");
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

        // 列表容器选择器
        parseConfig.setListSelector("li.pure-g.shadow");

        // 标题选择器
        parseConfig.setTitleSelector("h2 a");

        // 链接选择器
        parseConfig.setLinkSelector("h2 a");
        parseConfig.setLinkAttribute("href");

        // 内容选择器（包含导演、简介等）
        parseConfig.setContentSelector(".brief");
        parseConfig.setContentMode("html");

        // 日期选择器
        parseConfig.setDateSelector(".tags");
        parseConfig.setDateFormat("yyyy-MM-dd");

        // 图片选择器
        parseConfig.setImageSelector(".pure-u-5-24 img");
        parseConfig.setImageAttribute("data-original");

        // URL配置
        parseConfig.setNeedFullUrl(true);
        parseConfig.setUrlPrefix("https://www.rrdynb.com");

        // 自定义选择器
        Map<String, String> customSelectors = new HashMap<>();
        customSelectors.put("director", ".brief"); // 导演信息
        parseConfig.setCustomSelectors(customSelectors);

        return parseConfig;
    }

    /**
     * 构建RSS配置
     */
    private RssConfig buildRssConfig() {
        RssConfig rssConfig = new RssConfig();
        rssConfig.setTitle("人人影视网RSS订阅");
        rssConfig.setDescription("人人影视网最新内容");
        rssConfig.setLink("https://www.rrdynb.com");
        rssConfig.setLanguage("zh-CN");
        rssConfig.setGenerator("RSSNest");
        rssConfig.setItemsPerPage(20);
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
        cacheConfig.setTtl(10800L); // 3小时
        cacheConfig.setCacheNullValues(false);
        cacheConfig.setKeyPrefix("rrdynb");
        return cacheConfig;
    }
}
