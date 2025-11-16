package com.rss.nest.core.parser;

import com.rss.nest.core.config.ParseConfig;
import com.rss.nest.core.exception.HtmlParseException;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import com.rss.nest.utils.date.DateParseUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 可配置的HTML解析器
 * 基于CSS选择器配置进行HTML解析
 */
@Slf4j
@Component
public class ConfigurableHtmlParser implements HtmlParser {

    @Override
    public List<WebDataArticleDTO> parse(Document document, ParseConfig config) {
        List<WebDataArticleDTO> articles = new ArrayList<>();

        try {
            // 1. 选择列表容器
            Elements elements = document.select(config.getListSelector());
            if (elements.isEmpty()) {
                log.warn("未找到匹配的元素, 选择器: {}", config.getListSelector());
                return articles;
            }

            log.debug("找到 {} 个元素", elements.size());

            // 2. 遍历每个元素
            for (Element element : elements) {
                try {
                    WebDataArticleDTO article = parseElement(element, config);
                    if (article != null && isValidArticle(article)) {
                        articles.add(article);
                    }
                } catch (Exception e) {
                    log.warn("解析单个元素失败: {}", e.getMessage());
                    // 继续处理下一个元素
                }
            }

            log.info("成功解析 {} 条数据", articles.size());
            return articles;

        } catch (Exception e) {
            log.error("HTML解析失败", e);
            throw new HtmlParseException("HTML解析失败: " + e.getMessage(), e);
        }
    }

    /**
     * 解析单个元素
     */
    private WebDataArticleDTO parseElement(Element element, ParseConfig config) {
        WebDataArticleDTO article = new WebDataArticleDTO();

        // 提取标题（必填）
        String title = extractText(element, config.getTitleSelector());
        if (!StringUtils.hasText(title)) {
            log.debug("标题为空，跳过该元素");
            return null;
        }
        article.setTitle(title.trim());

        // 提取链接（必填）
        String url = extractAttribute(element, config.getLinkSelector(), config.getLinkAttribute());
        if (!StringUtils.hasText(url)) {
            log.debug("链接为空，跳过该元素");
            return null;
        }
        // 处理相对路径
        if (config.getNeedFullUrl() && !url.startsWith("http")) {
            url = buildFullUrl(url, config.getUrlPrefix());
        }
        article.setUrl(url);

        // 提取内容（可选）
        if (StringUtils.hasText(config.getContentSelector())) {
            String content = extractContent(element, config);
            article.setContent(content);
        }

        // 提取日期（可选）
        if (StringUtils.hasText(config.getDateSelector())) {
            Date publishTime = extractDate(element, config);
            article.setPublishTime(publishTime);
        }

        // 提取图片（可选）
        if (StringUtils.hasText(config.getImageSelector())) {
            String imageUrl = extractAttribute(element, config.getImageSelector(), config.getImageAttribute());
            if (StringUtils.hasText(imageUrl) && config.getNeedFullUrl() && !imageUrl.startsWith("http")) {
                imageUrl = buildFullUrl(imageUrl, config.getUrlPrefix());
            }
            article.setImageUrl(imageUrl);
        }

        // 提取作者（可选）
        if (StringUtils.hasText(config.getAuthorSelector())) {
            String author = extractText(element, config.getAuthorSelector());
            article.setAuthor(author);
        }

        // 提取分类（可选）
        if (StringUtils.hasText(config.getCategorySelector())) {
            String category = extractText(element, config.getCategorySelector());
            article.setCategory(category);
        }

        // 提取自定义字段（可选）
        if (config.getCustomSelectors() != null && !config.getCustomSelectors().isEmpty()) {
            config.getCustomSelectors().forEach((key, selector) -> {
                String value = extractText(element, selector);
                article.addCustomField(key, value);
            });
        }

        return article;
    }

    /**
     * 提取文本内容
     */
    private String extractText(Element element, String selector) {
        if (!StringUtils.hasText(selector)) {
            return null;
        }

        try {
            Elements selected = element.select(selector);
            if (selected.isEmpty()) {
                return null;
            }
            return selected.first().text();
        } catch (Exception e) {
            log.debug("提取文本失败, 选择器: {}, 错误: {}", selector, e.getMessage());
            return null;
        }
    }

    /**
     * 提取属性值
     */
    private String extractAttribute(Element element, String selector, String attribute) {
        if (!StringUtils.hasText(selector)) {
            return null;
        }

        try {
            Elements selected = element.select(selector);
            if (selected.isEmpty()) {
                return null;
            }
            return selected.first().attr(attribute);
        } catch (Exception e) {
            log.debug("提取属性失败, 选择器: {}, 属性: {}, 错误: {}", selector, attribute, e.getMessage());
            return null;
        }
    }

    /**
     * 提取内容（支持HTML或纯文本）
     */
    private String extractContent(Element element, ParseConfig config) {
        if (!StringUtils.hasText(config.getContentSelector())) {
            return null;
        }

        try {
            Elements selected = element.select(config.getContentSelector());
            if (selected.isEmpty()) {
                return null;
            }

            if ("html".equalsIgnoreCase(config.getContentMode())) {
                return selected.first().html();
            } else {
                return selected.first().text();
            }
        } catch (Exception e) {
            log.debug("提取内容失败, 选择器: {}, 错误: {}", config.getContentSelector(), e.getMessage());
            return null;
        }
    }

    /**
     * 提取日期
     */
    private Date extractDate(Element element, ParseConfig config) {
        String dateStr = extractText(element, config.getDateSelector());
        if (!StringUtils.hasText(dateStr)) {
            return null;
        }

        try {
            if (StringUtils.hasText(config.getDateFormat())) {
                return DateParseUtil.parseDateStr(config.getDateFormat(), dateStr.trim());
            } else {
                // 尝试自动解析
                return DateParseUtil.parseDate(dateStr.trim());
            }
        } catch (Exception e) {
            log.debug("日期解析失败, 日期字符串: {}, 格式: {}, 错误: {}",
                    dateStr, config.getDateFormat(), e.getMessage());
            return null;
        }
    }

    /**
     * 构建完整URL
     */
    private String buildFullUrl(String relativeUrl, String urlPrefix) {
        if (!StringUtils.hasText(urlPrefix)) {
            return relativeUrl;
        }

        // 处理相对路径
        if (relativeUrl.startsWith("/")) {
            return urlPrefix + relativeUrl;
        } else {
            return urlPrefix + "/" + relativeUrl;
        }
    }

    /**
     * 验证文章数据是否有效
     */
    private boolean isValidArticle(WebDataArticleDTO article) {
        return article != null
                && StringUtils.hasText(article.getTitle())
                && StringUtils.hasText(article.getUrl());
    }
}
