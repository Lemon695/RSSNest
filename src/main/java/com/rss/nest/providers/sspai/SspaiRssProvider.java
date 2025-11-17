package com.rss.nest.providers.sspai;

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

import java.util.*;
import java.util.stream.Collectors;

/**
 * 少数派RSS提供者
 * 使用少数派API获取文章数据
 */
@Slf4j
@Service
public class SspaiRssProvider extends AbstractRssProviderService {

    @Autowired
    private OkHttpClientUtil okHttpClientUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
        log.info("少数派RSS Provider初始化完成");
    }

    @Override
    public String getSiteIdentifier() {
        return "sspai";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        String category = params.getOrDefault("category", "index");

        SspaiCategoryEnum categoryEnum = SspaiCategoryEnum.getByCode(category);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("不支持的分类: " + category + "，支持的分类: index, matrix");
        }

        // 构建完整API URL，包含分页参数
        int limit = 15;
        int offset = 0;
        return categoryEnum.getApiUrl() + "?limit=" + limit + "&offset=" + offset;
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
        return SspaiCategoryEnum.isValidCode(category);
    }

    @Override
    public String getSupportedParams() {
        return "category: 分类（index-首页推荐, matrix-Matrix首页），默认: index";
    }

    /**
     * 重写generateRss方法以处理JSON API
     */
    @Override
    public RssChannel generateRss(Map<String, String> params) {
        try {
            log.info("开始生成少数派RSS, 参数: {}", params);

            // 1. 参数验证
            if (!validateParams(params)) {
                throw new IllegalArgumentException("参数验证失败: " + params);
            }

            // 2. 构建API URL
            String apiUrl = buildUrl(params);
            log.debug("请求API URL: {}", apiUrl);

            // 3. 调用API获取JSON数据
            String jsonResponse = fetchApiData(apiUrl);

            // 4. 解析JSON为文章列表
            List<SspaiArticleDTO.Article> articles = parseJsonToArticles(jsonResponse);
            log.info("获取到 {} 篇文章", articles.size());

            // 5. 转换为通用文章格式
            List<WebDataArticleDTO> webArticles = convertArticlesToWebData(articles);

            // 6. 构建WebHtmlData
            WebHtmlDataDTO webHtmlData = buildWebHtmlData(webArticles, params);

            // 7. 转换为RSS
            RssChannel rssChannel = convertToRss(webHtmlData);

            log.info("少数派RSS生成成功, 文章数: {}", articles.size());
            return rssChannel;

        } catch (Exception e) {
            log.error("少数派RSS生成失败", e);
            throw new RssGenerationException("少数派RSS生成失败: " + e.getMessage(), e);
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
            throw new HtmlFetchException("无法访问少数派API: " + apiUrl, e);
        }
    }

    /**
     * 解析JSON为文章列表
     */
    private List<SspaiArticleDTO.Article> parseJsonToArticles(String jsonResponse) {
        try {
            SspaiArticleDTO response = objectMapper.readValue(jsonResponse, SspaiArticleDTO.class);

            if (response.getData() == null || response.getData().getList() == null) {
                throw new RssGenerationException("少数派API返回数据为空");
            }

            return response.getData().getList();

        } catch (Exception e) {
            log.error("JSON解析失败", e);
            throw new RssGenerationException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将少数派文章转换为通用文章格式
     */
    private List<WebDataArticleDTO> convertArticlesToWebData(List<SspaiArticleDTO.Article> articles) {
        return articles.stream().map(article -> {
            WebDataArticleDTO webArticle = new WebDataArticleDTO();

            // 设置标题
            webArticle.setTitle(article.getTitle());

            // 设置链接
            String link = buildArticleLink(article);
            webArticle.setUrl(link);

            // 设置内容
            String content = buildArticleContent(article);
            webArticle.setContent(content);

            // 设置发布时间 - 将秒级时间戳转换为毫秒
            if (article.getReleasedAt() != null) {
                webArticle.setPublishTime(new Date(article.getReleasedAt() * 1000));
            } else if (article.getCreatedAt() != null) {
                webArticle.setPublishTime(new Date(article.getCreatedAt() * 1000));
            }

            // 设置作者
            if (article.getAuthor() != null) {
                webArticle.setAuthor(article.getAuthor().getNickname());
            }

            return webArticle;
        }).collect(Collectors.toList());
    }

    /**
     * 构建文章链接
     */
    private String buildArticleLink(SspaiArticleDTO.Article article) {
        return "https://sspai.com/post/" + article.getId();
    }

    /**
     * 构建文章内容HTML
     */
    private String buildArticleContent(SspaiArticleDTO.Article article) {
        StringBuilder content = new StringBuilder();

        // 添加宣传图
        if (article.getBanner() != null && !article.getBanner().isEmpty()) {
            content.append("<img src=\"").append(article.getBanner()).append("\" alt=\"").append(article.getTitle()).append("\" />");
            content.append("<br/>");
        }

        // 添加作者信息
        if (article.getAuthor() != null) {
            content.append("<p><strong>作者：</strong>").append(article.getAuthor().getNickname()).append("</p>");
        }

        // 添加摘要
        if (article.getSummary() != null && !article.getSummary().isEmpty()) {
            content.append("<p>").append(article.getSummary()).append("</p>");
        }

        // 添加会员标识
        if (article.getIsMember() != null && article.getIsMember()) {
            content.append("<p><em>【会员文章】</em></p>");
        }

        return content.toString();
    }

    @Override
    protected String getRssTitle(Map<String, String> params) {
        String category = params.getOrDefault("category", "index");
        SspaiCategoryEnum categoryEnum = SspaiCategoryEnum.getByCode(category);

        if (categoryEnum != null) {
            return "少数派 - " + categoryEnum.getName();
        }
        return "少数派 - 首页推荐";
    }

    @Override
    protected String getRssDescription(Map<String, String> params) {
        String category = params.getOrDefault("category", "index");
        SspaiCategoryEnum categoryEnum = SspaiCategoryEnum.getByCode(category);

        if (categoryEnum != null) {
            return "少数派" + categoryEnum.getName() + "最新文章";
        }
        return "少数派 - 高效工作，品质生活";
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Accept", "application/json");
        headers.put("Referer", "https://sspai.com/");
        return headers;
    }

    /**
     * 构建网站配置
     */
    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("sspai");
        config.setSiteName("少数派");
        config.setBaseUrl("https://sspai.com");
        config.setEnabled(true);

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
        rssConfig.setTitle("少数派RSS订阅");
        rssConfig.setDescription("少数派 - 高效工作，品质生活");
        rssConfig.setLink("https://sspai.com");
        rssConfig.setLanguage("zh-CN");
        rssConfig.setGenerator("RSSNest");
        rssConfig.setItemsPerPage(15);
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
        cacheConfig.setKeyPrefix("sspai");
        return cacheConfig;
    }
}
