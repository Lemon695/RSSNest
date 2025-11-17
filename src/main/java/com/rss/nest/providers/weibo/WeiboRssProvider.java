package com.rss.nest.providers.weibo;

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
 * 微博热搜RSS提供者
 * 使用微博移动端API获取热搜数据
 */
@Slf4j
@Service
public class WeiboRssProvider extends AbstractRssProviderService {

    @Autowired
    private OkHttpClientUtil okHttpClientUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_URL = "https://m.weibo.cn/api/container/getIndex?containerid=106003type%3D25%26t%3D3%26disable_hot%3D1%26filter_type%3Drealtimehot";
    private static final String WEB_URL = "https://s.weibo.com/top/summary";
    private static final String SEARCH_BASE_URL = "https://s.weibo.com/weibo?q=";

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
        log.info("微博热搜RSS Provider初始化完成");
    }

    @Override
    public String getSiteIdentifier() {
        return "weibo";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        return API_URL;
    }

    @Override
    public boolean validateParams(Map<String, String> params) {
        return true; // 微博热搜无需参数
    }

    @Override
    public String getSupportedParams() {
        return "无需参数，直接获取实时热搜榜";
    }

    /**
     * 重写generateRss方法以处理JSON API
     */
    @Override
    public RssChannel generateRss(Map<String, String> params) {
        try {
            log.info("开始生成微博热搜RSS");

            // 1. 调用API获取JSON数据
            String jsonResponse = fetchApiData(API_URL);

            // 2. 解析JSON为热搜列表
            List<WeiboHotSearchDTO.CardGroup> hotList = parseJsonToHotList(jsonResponse);
            log.info("获取到 {} 条热搜", hotList.size());

            // 3. 转换为通用文章格式
            List<WebDataArticleDTO> articles = convertHotListToArticles(hotList);

            // 4. 构建WebHtmlData
            WebHtmlDataDTO webHtmlData = buildWebHtmlData(articles, params);

            // 5. 转换为RSS
            RssChannel rssChannel = convertToRss(webHtmlData);

            log.info("微博热搜RSS生成成功, 热搜数: {}", hotList.size());
            return rssChannel;

        } catch (Exception e) {
            log.error("微博热搜RSS生成失败", e);
            throw new RssGenerationException("微博热搜RSS生成失败: " + e.getMessage(), e);
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
            throw new HtmlFetchException("无法访问微博API: " + apiUrl, e);
        }
    }

    /**
     * 解析JSON为热搜列表
     */
    private List<WeiboHotSearchDTO.CardGroup> parseJsonToHotList(String jsonResponse) {
        try {
            WeiboHotSearchDTO response = objectMapper.readValue(jsonResponse, WeiboHotSearchDTO.class);

            if (response.getData() == null || response.getData().getCards() == null) {
                throw new RssGenerationException("微博API返回数据为空");
            }

            // 获取第一个card的card_group
            for (WeiboHotSearchDTO.Card card : response.getData().getCards()) {
                if (card.getCardGroup() != null && !card.getCardGroup().isEmpty()) {
                    return card.getCardGroup();
                }
            }

            return Collections.emptyList();

        } catch (Exception e) {
            log.error("JSON解析失败", e);
            throw new RssGenerationException("JSON解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 将微博热搜转换为通用文章格式
     */
    private List<WebDataArticleDTO> convertHotListToArticles(List<WeiboHotSearchDTO.CardGroup> hotList) {
        return hotList.stream()
                .filter(item -> item.getDesc() != null && !item.getDesc().isEmpty())
                .map(item -> {
                    WebDataArticleDTO article = new WebDataArticleDTO();

                    // 设置标题
                    String title = buildTitle(item);
                    article.setTitle(title);

                    // 设置链接 - 使用搜索页面
                    String link = buildSearchLink(item.getDesc());
                    article.setUrl(link);

                    // 设置内容
                    String content = buildArticleContent(item);
                    article.setContent(content);

                    // 设置发布时间为当前时间
                    article.setPublishTime(new Date());

                    // 设置分类
                    if (item.getIconDesc() != null) {
                        article.setCategory(item.getIconDesc());
                    }

                    return article;
                }).collect(Collectors.toList());
    }

    /**
     * 构建标题
     */
    private String buildTitle(WeiboHotSearchDTO.CardGroup item) {
        StringBuilder title = new StringBuilder();

        // 添加排名
        if (item.getRank() != null) {
            title.append(item.getRank()).append(". ");
        }

        // 添加话题
        title.append(item.getDesc());

        // 添加热度标签
        if (item.getIconDesc() != null && !item.getIconDesc().isEmpty()) {
            title.append(" [").append(item.getIconDesc()).append("]");
        }

        return title.toString();
    }

    /**
     * 构建搜索链接
     */
    private String buildSearchLink(String keyword) {
        try {
            // URL编码关键词
            String encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8");
            return SEARCH_BASE_URL + encodedKeyword;
        } catch (Exception e) {
            log.warn("URL编码失败", e);
            return WEB_URL;
        }
    }

    /**
     * 构建文章内容HTML
     */
    private String buildArticleContent(WeiboHotSearchDTO.CardGroup item) {
        StringBuilder content = new StringBuilder();

        // 添加话题标题
        content.append("<h3>").append(item.getDesc()).append("</h3>");

        // 添加热度信息
        if (item.getIconDesc() != null && !item.getIconDesc().isEmpty()) {
            content.append("<p><strong>热度：</strong>").append(item.getIconDesc()).append("</p>");
        }

        // 添加排名
        if (item.getRank() != null) {
            content.append("<p><strong>排名：</strong>第 ").append(item.getRank()).append(" 位</p>");
        }

        // 添加副标题
        if (item.getTitleSub() != null && !item.getTitleSub().isEmpty()) {
            content.append("<p>").append(item.getTitleSub()).append("</p>");
        }

        // 添加额外描述
        if (item.getDescExtr() != null && !item.getDescExtr().isEmpty()) {
            content.append("<p>").append(item.getDescExtr()).append("</p>");
        }

        return content.toString();
    }

    @Override
    protected String getRssTitle(Map<String, String> params) {
        return "微博热搜榜";
    }

    @Override
    protected String getRssDescription(Map<String, String> params) {
        return "微博实时热搜榜单";
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        // 伪装成iPhone Safari
        headers.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_6 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.3 Mobile/15E148 Safari/604.1");
        headers.put("Accept", "application/json, text/plain, */*");
        headers.put("Referer", "https://s.weibo.com/top/summary?cate=realtimehot");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        return headers;
    }

    /**
     * 构建网站配置
     */
    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("weibo");
        config.setSiteName("微博热搜");
        config.setBaseUrl(WEB_URL);
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
        rssConfig.setTitle("微博热搜榜RSS订阅");
        rssConfig.setDescription("微博实时热搜榜单");
        rssConfig.setLink(WEB_URL);
        rssConfig.setLanguage("zh-CN");
        rssConfig.setGenerator("RSSNest");
        rssConfig.setItemsPerPage(50);
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
        cacheConfig.setTtl(300L); // 5分钟缓存，热搜更新很快
        cacheConfig.setCacheNullValues(false);
        cacheConfig.setKeyPrefix("weibo");
        return cacheConfig;
    }
}
