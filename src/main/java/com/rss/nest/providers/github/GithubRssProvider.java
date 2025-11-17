package com.rss.nest.providers.github;

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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GitHub Trending RSS提供者
 * 使用HTML解析获取GitHub趋势仓库
 */
@Slf4j
@Service
public class GithubRssProvider extends AbstractRssProviderService {

    @Autowired
    private OkHttpClientUtil okHttpClientUtil;

    private static final String BASE_URL = "https://github.com/trending";

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
        log.info("GitHub Trending RSS Provider初始化完成");
    }

    @Override
    public String getSiteIdentifier() {
        return "github";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        String since = params.getOrDefault("since", "daily");
        String language = params.getOrDefault("language", "");

        GithubCategoryEnum categoryEnum = GithubCategoryEnum.getByCode(since);
        if (categoryEnum == null) {
            throw new IllegalArgumentException("不支持的时间范围: " + since + "，支持的范围: daily, weekly, monthly");
        }

        // 构建URL
        StringBuilder url = new StringBuilder(BASE_URL);

        // 添加语言参数
        if (language != null && !language.isEmpty()) {
            url.append("/").append(language);
        }

        // 添加时间范围参数
        url.append("?since=").append(categoryEnum.getSince());

        return url.toString();
    }

    @Override
    public boolean validateParams(Map<String, String> params) {
        if (params == null) {
            return true; // 允许空参数，使用默认值
        }

        String since = params.get("since");
        if (since != null && !GithubCategoryEnum.isValidCode(since)) {
            return false;
        }

        return true;
    }

    @Override
    public String getSupportedParams() {
        return "since: 时间范围（daily-今日, weekly-本周, monthly-本月），默认: daily\n" +
               "language: 编程语言（如java, python, javascript等），默认: 全部语言";
    }

    /**
     * 重写generateRss方法以处理GitHub Trending
     */
    @Override
    public RssChannel generateRss(Map<String, String> params) {
        try {
            log.info("开始生成GitHub Trending RSS, 参数: {}", params);

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
            Document document = Jsoup.parse(html);

            // 5. 提取仓库列表
            List<WebDataArticleDTO> repos = extractRepos(document, params);
            log.info("提取到 {} 个仓库", repos.size());

            // 6. 构建WebHtmlData
            WebHtmlDataDTO webHtmlData = buildWebHtmlData(repos, params);

            // 7. 转换为RSS
            RssChannel rssChannel = convertToRss(webHtmlData);

            log.info("GitHub Trending RSS生成成功, 仓库数: {}", repos.size());
            return rssChannel;

        } catch (Exception e) {
            log.error("GitHub Trending RSS生成失败", e);
            throw new RssGenerationException("GitHub Trending RSS生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 提取仓库列表
     */
    private List<WebDataArticleDTO> extractRepos(Document document, Map<String, String> params) {
        List<WebDataArticleDTO> repos = new ArrayList<>();

        // 选择所有仓库article元素
        Elements articles = document.select("article.Box-row");

        for (Element article : articles) {
            try {
                WebDataArticleDTO repo = extractRepo(article);
                if (repo != null) {
                    repos.add(repo);
                }
            } catch (Exception e) {
                log.warn("解析仓库失败", e);
            }
        }

        return repos;
    }

    /**
     * 提取单个仓库信息
     */
    private WebDataArticleDTO extractRepo(Element article) {
        WebDataArticleDTO repo = new WebDataArticleDTO();

        // 提取仓库名称和链接
        Element h2 = article.selectFirst("h2 a");
        if (h2 == null) {
            return null;
        }

        String repoPath = h2.attr("href").trim();
        String repoFullName = repoPath.replaceFirst("^/", "");
        String repoUrl = "https://github.com" + repoPath;

        repo.setTitle(repoFullName);
        repo.setUrl(repoUrl);

        // 提取仓库描述
        Element descElement = article.selectFirst("p.col-9");
        String description = descElement != null ? descElement.text().trim() : "";

        // 提取语言
        Element langElement = article.selectFirst("span[itemprop=programmingLanguage]");
        String language = langElement != null ? langElement.text().trim() : "";

        // 提取Star数
        Element starsElement = article.selectFirst("svg.octicon-star");
        String stars = "";
        if (starsElement != null && starsElement.parent() != null) {
            stars = starsElement.parent().text().trim();
        }

        // 提取Fork数
        Element forksElement = article.selectFirst("svg.octicon-repo-forked");
        String forks = "";
        if (forksElement != null && forksElement.parent() != null) {
            forks = forksElement.parent().text().trim();
        }

        // 提取今日/本周/本月的Star数
        Element todayStarsElement = article.selectFirst("span.d-inline-block.float-sm-right");
        String todayStars = todayStarsElement != null ? todayStarsElement.text().trim() : "";

        // 构建内容
        String content = buildRepoContent(repoFullName, description, language, stars, forks, todayStars, repoUrl);
        repo.setContent(content);

        // 设置发布时间为当前时间
        repo.setPublishTime(new Date());

        // 设置分类为语言
        if (!language.isEmpty()) {
            repo.setCategory(language);
        }

        return repo;
    }

    /**
     * 构建仓库内容HTML
     */
    private String buildRepoContent(String repoName, String description, String language,
                                      String stars, String forks, String todayStars, String url) {
        StringBuilder content = new StringBuilder();

        // 添加仓库名称
        content.append("<h3><a href=\"").append(url).append("\">").append(repoName).append("</a></h3>");

        // 添加描述
        if (!description.isEmpty()) {
            content.append("<p>").append(description).append("</p>");
        }

        // 添加统计信息
        content.append("<p>");
        if (!language.isEmpty()) {
            content.append("<strong>语言：</strong>").append(language).append(" | ");
        }
        if (!stars.isEmpty()) {
            content.append("<strong>⭐ Stars：</strong>").append(stars).append(" | ");
        }
        if (!forks.isEmpty()) {
            content.append("<strong>🍴 Forks：</strong>").append(forks).append(" | ");
        }
        if (!todayStars.isEmpty()) {
            content.append("<strong>📈 ").append(todayStars).append("</strong>");
        }
        content.append("</p>");

        return content.toString();
    }

    @Override
    protected String getRssTitle(Map<String, String> params) {
        String since = params.getOrDefault("since", "daily");
        String language = params.getOrDefault("language", "");

        GithubCategoryEnum categoryEnum = GithubCategoryEnum.getByCode(since);
        String timeName = categoryEnum != null ? categoryEnum.getName() : "今日趋势";

        if (!language.isEmpty()) {
            return "GitHub Trending - " + language + " - " + timeName;
        }
        return "GitHub Trending - " + timeName;
    }

    @Override
    protected String getRssDescription(Map<String, String> params) {
        String since = params.getOrDefault("since", "daily");
        String language = params.getOrDefault("language", "");

        GithubCategoryEnum categoryEnum = GithubCategoryEnum.getByCode(since);
        String timeName = categoryEnum != null ? categoryEnum.getName() : "今日";

        if (!language.isEmpty()) {
            return "GitHub " + language + " " + timeName + "趋势仓库";
        }
        return "GitHub " + timeName + "趋势仓库";
    }

    @Override
    protected Map<String, String> getRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.put("Referer", "https://github.com/");
        return headers;
    }

    /**
     * 构建网站配置
     */
    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("github");
        config.setSiteName("GitHub Trending");
        config.setBaseUrl(BASE_URL);
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
        rssConfig.setTitle("GitHub Trending RSS订阅");
        rssConfig.setDescription("GitHub趋势仓库");
        rssConfig.setLink(BASE_URL);
        rssConfig.setLanguage("en");
        rssConfig.setGenerator("RSSNest");
        rssConfig.setItemsPerPage(25);
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
        cacheConfig.setTtl(3600L); // 1小时缓存
        cacheConfig.setCacheNullValues(false);
        cacheConfig.setKeyPrefix("github");
        return cacheConfig;
    }
}
