package com.rss.nest.providers.v2ex;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rss.nest.core.config.CacheConfig;
import com.rss.nest.core.config.RssConfig;
import com.rss.nest.core.config.SiteConfig;
import com.rss.nest.core.exception.HtmlFetchException;
import com.rss.nest.core.exception.RssGenerationException;
import com.rss.nest.core.provider.AbstractRssProviderService;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
import com.rss.nest.utils.http.OkHttpClientUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * V2EX RSS提供者
 * 使用V2EX官方API获取主题数据
 */
@Slf4j
@Service
public class V2exRssProvider extends AbstractRssProviderService {

    @Autowired
    private OkHttpClientUtil okHttpClientUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
        log.info("V2EX RSS Provider初始化完成");
    }

    @Override
    public String getSiteIdentifier() {
        return "v2ex";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        String category = params.getOrDefault("category", "hot");

        V2exCategoryEnum categoryEnum = V2exCategoryEnum.getByCode(category);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("不支持的分类: " + category + "，支持的分类: hot, latest");
        }

        return categoryEnum.getApiUrl();
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
        return V2exCategoryEnum.isValidCode(category);
    }

    @Override
    public String getSupportedParams() {
        return "category: 分类（hot-最热主题, latest-最新主题），默认: hot";
    }

    /**
     * 重写generateRss方法以处理JSON API
     * V2EX使用JSON API而不是HTML，所以需要自定义实现
     */
    @Override
    public RssChannel generateRss(Map<String, String> params) {
        try {
            log.info("开始生成V2EX RSS, 参数: {}", params);

            // 1. 参数验证
            if (!validateParams(params)) {
                throw new IllegalArgumentException("参数验证失败: " + params);
            }

            // 2. 构建API URL
            String apiUrl = buildUrl(params);
            log.debug("请求API URL: {}", apiUrl);

            // 3. 调用API获取JSON数据
            String jsonResponse = fetchApiData(apiUrl);

            // 4. 解析JSON为主题列表
            List<V2exTopicDTO> topics = parseJsonToTopics(jsonResponse);
            log.info("获取到 {} 个主题", topics.size());

            // 5. 转换为通用文章格式
            List<WebDataArticleDTO> articles = convertTopicsToArticles(topics);

            // 6. 构建WebHtmlData
            WebHtmlDataDTO webHtmlData = buildWebHtmlData(articles, params);

            // 7. 转换为RSS
            RssChannel rssChannel = convertToRss(webHtmlData);

            log.info("V2EX RSS生成成功, 主题数: {}", topics.size());
            return rssChannel;

        } catch (Exception e) {
            log.error("V2EX RSS生成失败", e);
            throw new RssGenerationException("V2EX RSS生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 调用API获取数据
     */
    private String fetchApiData(String apiUrl) {
        try {
            Map<String, String> headers = getRequestHeaders();
            return okHttpClientUtil.doGet(apiUrl, Headers.of(headers));
        } catch (Exception e) {
            log.error("API调用失败, URL: {}", apiUrl, e);
            throw new HtmlFetchException("无法访问V2EX API: " + apiUrl, e);
        }
    }

    /**
     * 解析JSON为主题列表
     */
    private List<V2exTopicDTO> parseJsonToTopics(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, new TypeReference<List<V2exTopicDTO>>() {});
        } catch (Exception e) {
            log.error("JSON解析失败", e);
            throw new RssGenerationException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将V2EX主题转换为通用文章格式
     */
    private List<WebDataArticleDTO> convertTopicsToArticles(List<V2exTopicDTO> topics) {
        return topics.stream().map(topic -> {
            WebDataArticleDTO article = new WebDataArticleDTO();

            // 设置标题
            article.setTitle(topic.getTitle());

            // 设置链接
            article.setUrl(topic.getUrl());

            // 设置内容 - 使用HTML渲染版本，如果没有则使用纯文本
            String content = buildArticleContent(topic);
            article.setContent(content);

            // 设置发布时间 - 将Unix时间戳（秒）转换为Date
            if (topic.getCreated() != null) {
                article.setPublishTime(new Date(topic.getCreated() * 1000));
            }

            // 设置作者
            if (topic.getMember() != null) {
                article.setAuthor(topic.getMember().getUsername());
            }

            // 设置分类
            if (topic.getNode() != null) {
                article.setCategory(topic.getNode().getTitle());
            }

            return article;
        }).collect(Collectors.toList());
    }

    /**
     * 构建文章内容HTML
     */
    private String buildArticleContent(V2exTopicDTO topic) {
        StringBuilder content = new StringBuilder();

        // 添加作者和节点信息
        if (topic.getMember() != null) {
            content.append("<p><strong>作者：</strong>").append(topic.getMember().getUsername()).append("</p>");
        }

        if (topic.getNode() != null) {
            content.append("<p><strong>节点：</strong>").append(topic.getNode().getTitle()).append("</p>");
        }

        // 添加回复数
        if (topic.getReplies() != null && topic.getReplies() > 0) {
            content.append("<p><strong>回复数：</strong>").append(topic.getReplies()).append("</p>");
        }

        // 添加内容
        if (topic.getContentRendered() != null && !topic.getContentRendered().isEmpty()) {
            content.append("<div>").append(topic.getContentRendered()).append("</div>");
        } else if (topic.getContent() != null && !topic.getContent().isEmpty()) {
            content.append("<p>").append(topic.getContent()).append("</p>");
        }

        return content.toString();
    }

    @Override
    protected String getRssTitle(Map<String, String> params) {
        String category = params.getOrDefault("category", "hot");
        V2exCategoryEnum categoryEnum = V2exCategoryEnum.getByCode(category);

        if (categoryEnum != null) {
            return "V2EX - " + categoryEnum.getName();
        }
        return "V2EX - 社区热门";
    }

    @Override
    protected String getRssDescription(Map<String, String> params) {
        String category = params.getOrDefault("category", "hot");
        V2exCategoryEnum categoryEnum = V2exCategoryEnum.getByCode(category);

        if (categoryEnum != null) {
            return "V2EX " + categoryEnum.getName() + "内容";
        }
        return "V2EX 创意工作者社区";
    }

    /**
     * 构建网站配置
     */
    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("v2ex");
        config.setSiteName("V2EX");
        config.setBaseUrl("https://www.v2ex.com");
        config.setEnabled(true);

        // 设置请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Accept", "application/json");
        config.setHeaders(headers);

        // 设置RSS配置
        config.setRssConfig(buildRssConfig());

        // 设置缓存配置
        config.setCacheConfig(buildCacheConfig());

        return config;
    }

    /**
     * 构建RSS配置
     */
    private RssConfig buildRssConfig() {
        RssConfig rssConfig = new RssConfig();
        rssConfig.setTitle("V2EX RSS订阅");
        rssConfig.setDescription("V2EX创意工作者社区");
        rssConfig.setLink("https://www.v2ex.com");
        rssConfig.setLanguage("zh-CN");
        rssConfig.setGenerator("RSSNest");
        rssConfig.setItemsPerPage(20);
        rssConfig.setIncludeContent(true);
        rssConfig.setIncludeImage(false);
        return rssConfig;
    }

    /**
     * 构建缓存配置
     */
    private CacheConfig buildCacheConfig() {
        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setEnabled(true);
        cacheConfig.setTtl(600L); // 10分钟缓存，V2EX更新较快
        cacheConfig.setCacheNullValues(false);
        cacheConfig.setKeyPrefix("v2ex");
        return cacheConfig;
    }
}
