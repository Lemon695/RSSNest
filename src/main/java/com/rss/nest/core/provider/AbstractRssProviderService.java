package com.rss.nest.core.provider;

import com.rss.nest.core.config.SiteConfig;
import com.rss.nest.core.exception.HtmlFetchException;
import com.rss.nest.core.exception.HtmlParseException;
import com.rss.nest.core.exception.RssGenerationException;
import com.rss.nest.core.parser.HtmlParser;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
import com.rss.nest.utils.http.OkHttpClientUtil;
import com.rss.nest.utils.web.HtmlDataConvertToRssUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * RSS提供者服务抽象基类
 * 实现通用的RSS生成流程，子类只需实现特定的业务逻辑
 */
@Slf4j
public abstract class AbstractRssProviderService implements RssProviderService {

    @Autowired
    protected OkHttpClientUtil okHttpClientUtil;

    @Autowired
    protected HtmlParser htmlParser;

    /**
     * 网站配置（子类需要初始化）
     */
    protected SiteConfig siteConfig;

    /**
     * 模板方法：生成RSS的完整流程
     */
    @Override
    public RssChannel generateRss(Map<String, String> params) {
        try {
            log.info("开始生成RSS, 网站: {}, 参数: {}", getSiteIdentifier(), params);

            // 1. 参数验证
            if (!validateParams(params)) {
                throw new IllegalArgumentException("参数验证失败: " + params);
            }

            // 2. 构建URL
            String url = buildUrl(params);
            log.debug("请求URL: {}", url);

            // 3. 抓取HTML
            String html = fetchHtml(url);

            // 4. 解析HTML为Document
            Document document = parseHtmlToDocument(html);

            // 5. 提取数据
            List<WebDataArticleDTO> articles = extractArticles(document, params);
            log.info("提取到 {} 条数据", articles.size());

            // 6. 转换为通用数据格式
            WebHtmlDataDTO webHtmlData = buildWebHtmlData(articles, params);

            // 7. 转换为RSS
            RssChannel rssChannel = convertToRss(webHtmlData);

            log.info("RSS生成成功, 网站: {}, 条目数: {}", getSiteIdentifier(), articles.size());
            return rssChannel;

        } catch (HtmlFetchException | HtmlParseException | RssGenerationException e) {
            log.error("RSS生成失败, 网站: {}, 错误: {}", getSiteIdentifier(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("RSS生成异常, 网站: {}", getSiteIdentifier(), e);
            throw new RssGenerationException("RSS生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 构建请求URL
     * 子类可以重写此方法实现自定义URL构建逻辑
     *
     * @param params 请求参数
     * @return 完整URL
     */
    protected abstract String buildUrl(Map<String, String> params);

    /**
     * 提取文章数据
     * 子类可以重写此方法实现自定义解析逻辑
     * 默认使用配置化解析器
     *
     * @param document HTML文档
     * @param params   请求参数
     * @return 文章列表
     */
    protected List<WebDataArticleDTO> extractArticles(Document document, Map<String, String> params) {
        if (siteConfig.getParseConfig() != null) {
            // 使用配置化解析器
            return htmlParser.parse(document, siteConfig.getParseConfig());
        } else {
            // 子类必须重写此方法或提供parseConfig
            throw new UnsupportedOperationException(
                    "网站 " + getSiteIdentifier() + " 未配置parseConfig，需要重写extractArticles方法"
            );
        }
    }

    /**
     * 抓取HTML内容
     *
     * @param url 目标URL
     * @return HTML内容
     */
    protected String fetchHtml(String url) {
        try {
            Map<String, String> headers = getRequestHeaders();
            return okHttpClientUtil.doGet(url, Headers.of(headers));
        } catch (Exception e) {
            log.error("HTML抓取失败, URL: {}", url, e);
            throw new HtmlFetchException("无法访问网站: " + url, e);
        }
    }

    /**
     * 解析HTML为Document对象
     *
     * @param html HTML字符串
     * @return Jsoup Document
     */
    protected Document parseHtmlToDocument(String html) {
        try {
            return Jsoup.parse(html);
        } catch (Exception e) {
            log.error("HTML解析失败", e);
            throw new HtmlParseException("HTML解析失败", e);
        }
    }

    /**
     * 构建WebHtmlData对象
     *
     * @param articles 文章列表
     * @param params   请求参数
     * @return WebHtmlDataDTO对象
     */
    protected WebHtmlDataDTO buildWebHtmlData(List<WebDataArticleDTO> articles, Map<String, String> params) {
        WebHtmlDataDTO webHtmlData = new WebHtmlDataDTO();
        webHtmlData.setArticleList(articles);
        webHtmlData.setTitle(getRssTitle(params));
        webHtmlData.setLink(siteConfig.getBaseUrl());
        webHtmlData.setDescription(getRssDescription(params));
        return webHtmlData;
    }

    /**
     * 转换为RSS Channel
     *
     * @param webHtmlData Web数据
     * @return RSS Channel
     */
    protected RssChannel convertToRss(WebHtmlDataDTO webHtmlData) {
        try {
            return HtmlDataConvertToRssUtil.analysisDocDataToRss(webHtmlData);
        } catch (Exception e) {
            log.error("RSS转换失败", e);
            throw new RssGenerationException("RSS转换失败", e);
        }
    }

    /**
     * 获取请求头
     * 子类可以重写此方法自定义请求头
     *
     * @return 请求头Map
     */
    protected Map<String, String> getRequestHeaders() {
        if (siteConfig.getHeaders() != null) {
            return siteConfig.getHeaders();
        }
        return Map.of(
                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
        );
    }

    /**
     * 获取RSS标题
     * 子类可以重写此方法实现动态标题
     *
     * @param params 请求参数
     * @return RSS标题
     */
    protected String getRssTitle(Map<String, String> params) {
        if (siteConfig.getRssConfig() != null && siteConfig.getRssConfig().getTitle() != null) {
            return siteConfig.getRssConfig().getTitle();
        }
        return siteConfig.getSiteName() + " - RSS订阅";
    }

    /**
     * 获取RSS描述
     * 子类可以重写此方法实现动态描述
     *
     * @param params 请求参数
     * @return RSS描述
     */
    protected String getRssDescription(Map<String, String> params) {
        if (siteConfig.getRssConfig() != null && siteConfig.getRssConfig().getDescription() != null) {
            return siteConfig.getRssConfig().getDescription();
        }
        return siteConfig.getSiteName() + "最新内容";
    }

    @Override
    public SiteConfig getSiteConfig() {
        return siteConfig;
    }
}
