package com.rss.nest.core.parser;

import com.rss.nest.core.config.ParseConfig;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置化HTML解析器测试
 */
class ConfigurableHtmlParserTest {

    private ConfigurableHtmlParser parser;
    private ParseConfig parseConfig;

    @BeforeEach
    void setUp() {
        parser = new ConfigurableHtmlParser();
        parseConfig = new ParseConfig();
    }

    @Test
    void testParseBasicHtml() {
        // 准备测试HTML
        String html = """
                <html>
                <body>
                    <div class="article-list">
                        <article class="item">
                            <h2 class="title">测试标题1</h2>
                            <a class="link" href="/article/1">查看详情</a>
                            <div class="content">这是测试内容1</div>
                            <span class="date">2024-01-01</span>
                        </article>
                        <article class="item">
                            <h2 class="title">测试标题2</h2>
                            <a class="link" href="/article/2">查看详情</a>
                            <div class="content">这是测试内容2</div>
                            <span class="date">2024-01-02</span>
                        </article>
                    </div>
                </body>
                </html>
                """;

        Document document = Jsoup.parse(html);

        // 配置解析器
        parseConfig.setListSelector("article.item");
        parseConfig.setTitleSelector("h2.title");
        parseConfig.setLinkSelector("a.link");
        parseConfig.setContentSelector("div.content");
        parseConfig.setDateSelector("span.date");
        parseConfig.setDateFormat("yyyy-MM-dd");
        parseConfig.setNeedFullUrl(true);
        parseConfig.setUrlPrefix("https://example.com");

        // 执行解析
        List<WebDataArticleDTO> articles = parser.parse(document, parseConfig);

        // 验证结果
        assertNotNull(articles);
        assertEquals(2, articles.size());

        WebDataArticleDTO article1 = articles.get(0);
        assertEquals("测试标题1", article1.getTitle());
        assertEquals("https://example.com/article/1", article1.getUrl());
        assertEquals("这是测试内容1", article1.getContent());
        assertNotNull(article1.getPublishTime());

        WebDataArticleDTO article2 = articles.get(1);
        assertEquals("测试标题2", article2.getTitle());
        assertEquals("https://example.com/article/2", article2.getUrl());
    }

    @Test
    void testParseWithMissingElements() {
        String html = """
                <html>
                <body>
                    <article class="item">
                        <h2 class="title">只有标题</h2>
                    </article>
                </body>
                </html>
                """;

        Document document = Jsoup.parse(html);

        parseConfig.setListSelector("article.item");
        parseConfig.setTitleSelector("h2.title");
        parseConfig.setLinkSelector("a.link");

        List<WebDataArticleDTO> articles = parser.parse(document, parseConfig);

        // 缺少必填的link，应该被过滤掉
        assertEquals(0, articles.size());
    }

    @Test
    void testParseWithImage() {
        String html = """
                <html>
                <body>
                    <article class="item">
                        <h2>文章标题</h2>
                        <a href="/test">链接</a>
                        <img class="cover" src="/image.jpg" />
                    </article>
                </body>
                </html>
                """;

        Document document = Jsoup.parse(html);

        parseConfig.setListSelector("article.item");
        parseConfig.setTitleSelector("h2");
        parseConfig.setLinkSelector("a");
        parseConfig.setImageSelector("img.cover");
        parseConfig.setImageAttribute("src");
        parseConfig.setNeedFullUrl(true);
        parseConfig.setUrlPrefix("https://example.com");

        List<WebDataArticleDTO> articles = parser.parse(document, parseConfig);

        assertEquals(1, articles.size());
        assertEquals("https://example.com/image.jpg", articles.get(0).getImageUrl());
    }
}
