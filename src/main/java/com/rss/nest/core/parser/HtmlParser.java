package com.rss.nest.core.parser;

import com.rss.nest.core.config.ParseConfig;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import org.jsoup.nodes.Document;

import java.util.List;

/**
 * HTML解析器接口
 */
public interface HtmlParser {

    /**
     * 解析HTML文档为文章列表
     *
     * @param document    Jsoup Document对象
     * @param parseConfig 解析配置
     * @return 文章列表
     */
    List<WebDataArticleDTO> parse(Document document, ParseConfig parseConfig);
}
