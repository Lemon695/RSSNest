# RSSNest 架构文档 v2.0

## 概述

RSSNest v2.0 采用全新的模块化、可配置的架构设计，极大地提升了系统的扩展性和可维护性。

## 核心架构

### 1. 分层架构

```
┌─────────────────────────────────────────┐
│         Controller Layer                │  统一控制器
│         (UnifiedRssController)          │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│       Service Layer                     │  带缓存的RSS服务
│       (CacheableRssService)             │
└──────────────────┬──────────────────────┘
                   │
         ┌─────────┴─────────┐
         │                   │
┌────────▼────────┐ ┌────────▼────────┐
│ Provider Factory│ │  Cache Service  │  工厂模式 + 缓存服务
│                 │ │                 │
└────────┬────────┘ └─────────────────┘
         │
┌────────▼────────────────────────────────┐
│         Provider Layer                  │  各网站的Provider实现
│  (AbstractRssProviderService)           │
└──────────────────┬──────────────────────┘
                   │
         ┌─────────┴─────────┐
         │                   │
┌────────▼────────┐ ┌────────▼────────┐
│  HTML Parser    │ │  HTTP Client    │  解析器 + HTTP客户端
│                 │ │                 │
└─────────────────┘ └─────────────────┘
```

### 2. 核心组件

#### 2.1 Provider 系统

**RssProviderService** - 核心服务接口
- 定义了RSS生成的标准流程
- 所有网站Provider都需要实现此接口

**AbstractRssProviderService** - 抽象基类
- 实现了通用的RSS生成模板方法
- 提供了默认的HTTP抓取、HTML解析、RSS转换等功能
- 子类只需实现少量必要方法即可

**RssProviderFactory** - 工厂类
- 自动注册所有Provider
- 根据siteId获取对应的Provider
- 提供Provider信息查询功能

#### 2.2 解析器系统

**ConfigurableHtmlParser** - 配置化解析器
- 基于CSS选择器的配置化解析
- 支持各种HTML结构的灵活解析
- 自动处理空值和异常情况

**ParseConfig** - 解析配置
- 列表选择器、标题、链接、内容等
- 支持自定义字段
- 支持相对路径转绝对路径

#### 2.3 缓存系统

**RssCacheService** - 缓存服务接口
- 统一的缓存操作接口
- 支持防击穿的`getOrSet`方法

**RssCacheServiceImpl** - Redis缓存实现
- 使用分布式锁防止缓存击穿
- 双重检查机制
- 自动降级策略

**CacheableRssService** - 带缓存的RSS服务
- 统一处理缓存逻辑
- 根据配置决定是否启用缓存
- 提供缓存清理功能

#### 2.4 异常处理

**GlobalExceptionHandler** - 全局异常处理器
- 统一处理所有异常
- 返回友好的错误信息
- 区分不同类型的异常返回不同的HTTP状态码

**异常体系：**
- `RssException` - 基础异常
- `UnsupportedSiteException` - 不支持的网站
- `HtmlFetchException` - HTML抓取失败
- `HtmlParseException` - HTML解析失败
- `RssGenerationException` - RSS生成失败

## 配置化设计

### SiteConfig - 网站配置

```java
{
  "siteId": "rrdynb",              // 网站ID
  "siteName": "人人影视网",         // 网站名称
  "baseUrl": "https://...",        // 基础URL
  "headers": {...},                // 请求头
  "parseConfig": {...},            // 解析配置
  "rssConfig": {...},              // RSS配置
  "cacheConfig": {...}             // 缓存配置
}
```

### ParseConfig - 解析配置

```java
{
  "listSelector": "li.item",       // 列表选择器
  "titleSelector": "h2.title",     // 标题选择器
  "linkSelector": "a.link",        // 链接选择器
  "contentSelector": ".content",   // 内容选择器
  "dateSelector": ".date",         // 日期选择器
  "dateFormat": "yyyy-MM-dd",      // 日期格式
  "imageSelector": "img",          // 图片选择器
  "customSelectors": {...}         // 自定义选择器
}
```

## 新增网站流程

### 方式一：完全配置化（推荐）

1. 创建Provider类继承`AbstractRssProviderService`
2. 实现`buildUrl()`方法构建URL
3. 在`@PostConstruct`方法中初始化配置
4. 完成！Spring会自动注册

**示例代码：**

```java
@Service
public class NewSiteRssProvider extends AbstractRssProviderService {

    @PostConstruct
    public void init() {
        this.siteConfig = buildSiteConfig();
    }

    @Override
    public String getSiteIdentifier() {
        return "newsite";
    }

    @Override
    protected String buildUrl(Map<String, String> params) {
        return siteConfig.getBaseUrl() + "/" + params.get("category");
    }

    private SiteConfig buildSiteConfig() {
        SiteConfig config = new SiteConfig();
        config.setSiteId("newsite");
        config.setSiteName("新网站");
        config.setBaseUrl("https://newsite.com");

        // 配置解析器
        ParseConfig parseConfig = new ParseConfig();
        parseConfig.setListSelector("div.article");
        parseConfig.setTitleSelector("h2");
        parseConfig.setLinkSelector("a");
        // ... 其他配置

        config.setParseConfig(parseConfig);
        return config;
    }
}
```

### 方式二：自定义解析（复杂网站）

如果网站结构特殊，可以重写`extractArticles()`方法：

```java
@Override
protected List<WebDataArticleDTO> extractArticles(Document document, Map<String, String> params) {
    // 自定义解析逻辑
    List<WebDataArticleDTO> articles = new ArrayList<>();
    // ... 复杂的解析逻辑
    return articles;
}
```

## API接口

### 统一接口

```bash
# 基础接口
GET /api/rss/{siteId}?param1=value1&param2=value2

# 带分类接口
GET /api/rss/{siteId}/{category}

# 示例
GET /api/rss/rrdynb/movie
GET /api/rss/rrdynb?category=movie
```

### 管理接口

```bash
# 获取支持的网站列表
GET /api/rss/sites

# 获取网站详情
GET /api/rss/sites/info

# 清除缓存
DELETE /api/rss/{siteId}/cache?param1=value1

# 健康检查
GET /api/rss/health
```

## 缓存策略

### 三级缓存策略

1. **前置拦截器缓存检查** - 最快返回
2. **Redis缓存** - 带分布式锁的防击穿
3. **源数据获取** - 缓存未命中时执行

### 缓存Key设计

```
格式: rssNest:cache:{siteId}:{paramsHash}
示例: rssNest:cache:rrdynb:a1b2c3d4e5f6
```

### 防击穿机制

1. 第一次请求获取分布式锁
2. 双重检查缓存
3. 执行数据获取并缓存
4. 其他请求等待锁释放后获取缓存

## 性能优化

### 1. 并发控制
- 分布式锁防止缓存击穿
- 锁超时自动释放

### 2. 缓存优化
- 可配置的缓存时间
- 支持按网站清空缓存
- 缓存Key使用MD5避免过长

### 3. 解析优化
- 异常不中断整体流程
- 单条解析失败继续处理下一条
- 自动过滤无效数据

## 监控和日志

### 日志级别

- **INFO** - 请求开始、RSS生成成功、Provider注册
- **DEBUG** - 缓存命中、URL构建、解析详情
- **WARN** - 解析失败、缓存获取失败
- **ERROR** - 异常情况、严重错误

### 关键监控点

1. RSS生成成功率
2. 缓存命中率
3. 平均响应时间
4. 解析失败次数
5. 异常发生频率

## 扩展点

### 1. 新增解析器类型
实现`HtmlParser`接口，支持其他解析方式（如XPath、正则等）

### 2. 新增缓存实现
实现`RssCacheService`接口，支持其他缓存（如Memcached、本地缓存等）

### 3. 新增数据源
不仅限于HTML，可以支持API、数据库等其他数据源

### 4. 中间件扩展
- 数据清洗中间件
- 内容增强中间件
- 反爬虫中间件

## 最佳实践

### 1. Provider开发
- 优先使用配置化解析
- 合理设置缓存时间
- 添加参数验证
- 提供清晰的参数说明

### 2. 性能优化
- 缓存时间根据更新频率设置
- 避免频繁清空缓存
- 监控解析性能

### 3. 错误处理
- 使用明确的异常类型
- 提供友好的错误信息
- 记录详细的错误日志

### 4. 测试
- 为每个Provider编写单元测试
- 测试各种边界情况
- 模拟网络异常情况

## 版本历史

### v2.0 (2024-11)
- 全新的模块化架构
- 配置化HTML解析器
- 统一的Provider接口
- 改进的缓存系统
- 全局异常处理
- 完整的单元测试

### v1.0
- 基础RSS生成功能
- 人人影视网支持
- Redis缓存
