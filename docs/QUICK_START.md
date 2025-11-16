# RSSNest 快速开始

## 项目启动

### 1. 环境要求

- Java 17+
- Maven 3.6+
- Redis 6.0+

### 2. 启动步骤

```bash
# 克隆项目
git clone https://github.com/Lemon695/RSSNest.git
cd RSSNest

# 配置Redis
# 编辑 src/main/resources/application-dev.yml
# 修改Redis连接信息

# 启动项目
mvn spring-boot:run
```

### 3. 访问接口文档

```
http://localhost:8080/doc.html
```

## 使用示例

### 获取RSS订阅

```bash
# 人人影视网 - 电影
curl http://localhost:8080/api/rss/rrdynb/movie

# 人人影视网 - 电视剧
curl http://localhost:8080/api/rss/rrdynb/dianshiju

# 人人影视网 - 动漫
curl http://localhost:8080/api/rss/rrdynb/dongman
```

### 查看支持的网站

```bash
curl http://localhost:8080/api/rss/sites
```

### 清除缓存

```bash
curl -X DELETE http://localhost:8080/api/rss/rrdynb/cache?category=movie
```

## 新增网站支持

### 示例：新增B站UP主视频RSS

#### 步骤1: 创建Provider

在 `src/main/java/com/rss/nest/providers/` 下创建新目录 `bilibili`

创建文件 `BilibiliUpRssProvider.java`:

```java
package com.rss.nest.providers.bilibili;

import com.rss.nest.core.config.*;
import com.rss.nest.core.provider.AbstractRssProviderService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class BilibiliUpRssProvider extends AbstractRssProviderService {

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
    }

    @Override
    public String getSiteIdentifier() {
        return "bilibili-up";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        String uid = params.get("uid");
        if (uid == null) {
            throw new IllegalArgumentException("缺少参数: uid");
        }
        return "https://space.bilibili.com/" + uid + "/video";
    }

    @Override
    public boolean validateParams(Map<String, String> params) {
        return params != null && params.containsKey("uid");
    }

    @Override
    public String getSupportedParams() {
        return "uid: B站UP主的UID（必填）";
    }

    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("bilibili-up");
        config.setSiteName("B站UP主视频");
        config.setBaseUrl("https://space.bilibili.com");

        // 配置请求头（重要！）
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 ...");
        headers.put("Referer", "https://www.bilibili.com");
        config.setHeaders(headers);

        // 配置解析器
        ParseConfig parseConfig = new ParseConfig();
        parseConfig.setListSelector("div.small-item");
        parseConfig.setTitleSelector("a.title");
        parseConfig.setLinkSelector("a");
        parseConfig.setLinkAttribute("href");
        parseConfig.setImageSelector("img");
        parseConfig.setImageAttribute("src");
        parseConfig.setNeedFullUrl(true);
        parseConfig.setUrlPrefix("https:");

        config.setParseConfig(parseConfig);

        // 配置缓存（B站更新频繁，缓存时间短一些）
        CacheConfig cacheConfig = new CacheConfig();
        cacheConfig.setEnabled(true);
        cacheConfig.setTtl(1800L); // 30分钟
        config.setCacheConfig(cacheConfig);

        return config;
    }
}
```

#### 步骤2: 重启应用

```bash
mvn spring-boot:run
```

#### 步骤3: 测试

```bash
# 获取某个UP主的视频RSS
curl http://localhost:8080/api/rss/bilibili-up?uid=123456

# 查看是否注册成功
curl http://localhost:8080/api/rss/sites
```

完成！就这么简单！

## 常见问题

### Q1: 如何调试解析器配置？

A: 可以临时启用DEBUG日志：

```yaml
logging:
  level:
    com.rss.nest.core.parser: DEBUG
```

查看日志中的解析详情。

### Q2: 网站反爬怎么办？

A:
1. 配置合适的User-Agent和Referer
2. 增加请求间隔（在Provider中实现延迟）
3. 使用代理池（扩展HTTP客户端）

### Q3: 如何处理动态加载的内容？

A:
1. 如果网站有API，直接请求API
2. 使用Selenium等工具渲染页面（需扩展）
3. 分析网络请求，找到数据接口

### Q4: 缓存时间如何设置？

A:
- 新闻类网站：10-30分钟
- 博客类网站：1-2小时
- 视频网站：30分钟-1小时
- 更新不频繁的：2-6小时

### Q5: 如何实现自定义解析逻辑？

A: 重写`extractArticles()`方法：

```java
@Override
protected List<WebDataArticleDTO> extractArticles(Document document, Map<String, String> params) {
    List<WebDataArticleDTO> articles = new ArrayList<>();

    // 你的自定义解析逻辑
    Elements items = document.select("your-selector");
    for (Element item : items) {
        WebDataArticleDTO article = new WebDataArticleDTO();
        // 提取数据
        article.setTitle(...);
        article.setUrl(...);
        articles.add(article);
    }

    return articles;
}
```

## 进阶技巧

### 1. 使用自定义字段

```java
ParseConfig parseConfig = new ParseConfig();
// ... 基础配置

// 添加自定义字段
Map<String, String> customSelectors = new HashMap<>();
customSelectors.put("views", ".view-count");
customSelectors.put("likes", ".like-count");
parseConfig.setCustomSelectors(customSelectors);
```

在RSS中可以通过`article.getCustomField("views")`获取。

### 2. 处理相对日期

如果网站显示的是"3小时前"、"昨天"等相对时间，需要自定义解析：

```java
@Override
protected List<WebDataArticleDTO> extractArticles(Document document, Map<String, String> params) {
    // ... 基础解析

    String relativeTime = element.select(".time").text();
    Date publishTime = parseRelativeTime(relativeTime);
    article.setPublishTime(publishTime);

    return articles;
}

private Date parseRelativeTime(String relativeTime) {
    // "3小时前" -> Date对象
    // 实现相对时间解析逻辑
}
```

### 3. 内容增强

```java
@Override
protected WebHtmlDataDTO buildWebHtmlData(List<WebDataArticleDTO> articles, Map<String, String> params) {
    // 增强每篇文章的内容
    for (WebDataArticleDTO article : articles) {
        String enhancedContent = enhanceContent(article.getContent());
        article.setContent(enhancedContent);
    }

    return super.buildWebHtmlData(articles, params);
}

private String enhanceContent(String content) {
    // 添加样式、格式化、过滤敏感词等
    return content;
}
```

## 下一步

- 阅读 [架构文档](./ARCHITECTURE.md) 了解详细设计
- 查看 [API文档](http://localhost:8080/doc.html) 了解所有接口
- 参考现有Provider实现学习最佳实践
