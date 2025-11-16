# RSSNest

<div align="center">

**RSS订阅源生成服务 - 将任何网站转换为RSS订阅**

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)

</div>

## ✨ 特性

- 🚀 **模块化架构** - 全新v2.0架构，易于扩展和维护
- 🎯 **配置化解析** - 基于CSS选择器的配置化HTML解析，无需编写复杂代码
- 💾 **智能缓存** - Redis缓存 + 防击穿机制，性能优异
- 🔌 **插件式扩展** - 新增网站支持只需创建一个Provider类
- 🛡️ **异常处理** - 完善的全局异常处理，友好的错误提示
- 📊 **统一接口** - RESTful API设计，接口简洁统一
- 📝 **完整文档** - 详细的架构文档和快速开始指南
- ✅ **测试覆盖** - 核心功能单元测试

## 📚 目录

- [快速开始](#-快速开始)
- [架构设计](#-架构设计)
- [支持网站](#-支持网站)
- [API接口](#-api接口)
- [新增网站](#-新增网站支持)
- [文档](#-文档)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)

## 🚀 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- Redis 6.0+

### 启动步骤

```bash
# 1. 克隆项目
git clone https://github.com/Lemon695/RSSNest.git
cd RSSNest

# 2. 配置Redis
# 编辑 src/main/resources/application-dev.yml
vim src/main/resources/application-dev.yml

# 3. 启动项目
mvn clean spring-boot:run

# 4. 访问接口文档
# http://localhost:8080/doc.html
```

### 快速使用

```bash
# 获取人人影视网电影RSS
curl http://localhost:8080/api/rss/rrdynb/movie

# 获取支持的网站列表
curl http://localhost:8080/api/rss/sites

# 健康检查
curl http://localhost:8080/api/rss/health
```

## 🏗️ 架构设计

RSSNest v2.0 采用分层架构 + 工厂模式 + 策略模式的设计：

```
Controller → CacheableRssService → RssProviderFactory → Provider → Parser
```

### 核心组件

| 组件 | 职责 | 说明 |
|------|------|------|
| **UnifiedRssController** | 统一控制器 | 提供RESTful API |
| **CacheableRssService** | 缓存服务 | 处理缓存逻辑 |
| **RssProviderFactory** | Provider工厂 | 管理所有Provider |
| **AbstractRssProviderService** | 抽象Provider | 模板方法模式 |
| **ConfigurableHtmlParser** | 配置化解析器 | 基于CSS选择器解析 |
| **RssCacheService** | Redis缓存 | 防击穿机制 |

详细架构文档: [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)

## 🌐 支持网站

| 网站 | SiteID | 分类 | 状态 |
|------|--------|------|------|
| 人人影视网 | `rrdynb` | 电影、电视剧、综艺、动漫 | ✅ 可用 |

**持续添加中...**

## 📡 API接口

### 基础接口

```http
GET /api/rss/{siteId}
GET /api/rss/{siteId}/{category}
GET /api/rss/sites
GET /api/rss/sites/info
DELETE /api/rss/{siteId}/cache
GET /api/rss/health
```

### 使用示例

#### 人人影视网

```bash
# 电影
curl http://localhost:8080/api/rss/rrdynb/movie

# 电视剧
curl http://localhost:8080/api/rss/rrdynb/dianshiju

# 动漫
curl http://localhost:8080/api/rss/rrdynb/dongman

# 综艺
curl http://localhost:8080/api/rss/rrdynb/zongyi
```

完整API文档: http://localhost:8080/doc.html

## 🔧 新增网站支持

只需3步即可新增网站支持：

### 1. 创建Provider类

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
        // 配置网站信息、解析规则、缓存策略
    }
}
```

### 2. 配置解析规则

```java
ParseConfig parseConfig = new ParseConfig();
parseConfig.setListSelector("div.article-list > article");
parseConfig.setTitleSelector("h2.title");
parseConfig.setLinkSelector("a.link");
parseConfig.setContentSelector("div.content");
parseConfig.setDateSelector("span.date");
parseConfig.setDateFormat("yyyy-MM-dd");
```

### 3. 启动测试

```bash
mvn spring-boot:run
curl http://localhost:8080/api/rss/newsite
```

详细教程: [docs/QUICK_START.md](docs/QUICK_START.md)

## 📖 文档

- [快速开始指南](docs/QUICK_START.md) - 快速上手和示例
- [架构设计文档](docs/ARCHITECTURE.md) - 详细的架构说明
- [API文档](http://localhost:8080/doc.html) - 在线接口文档

## 🤝 贡献指南

欢迎贡献新的网站支持！

1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/new-site`)
3. 提交更改 (`git commit -am 'Add new site support'`)
4. 推送到分支 (`git push origin feature/new-site`)
5. 创建 Pull Request

### 贡献新网站

1. 在 `src/main/java/com/rss/nest/providers/` 下创建新目录
2. 实现Provider类
3. 编写测试用例
4. 更新README的支持网站列表
5. 提交PR

## 🔗 相关项目

- [RSSHub](https://github.com/DIYgod/RSSHub) - 万物皆可RSS
- [Huginn](https://github.com/huginn/huginn) - 自动化工作流
- [FreshRSS](https://github.com/FreshRSS/FreshRSS) - RSS阅读器

## 📝 更新日志

### v2.0.0 (2024-11)

- 🎉 全新架构重构
- ✨ 配置化HTML解析器
- 🚀 统一Provider接口
- 💾 改进的缓存系统
- 🛡️ 全局异常处理
- 📝 完整文档和测试

### v1.0.0

- 基础RSS生成功能
- 人人影视网支持
- Redis缓存

## 📄 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。

## 💬 联系方式

- Issue: [GitHub Issues](https://github.com/Lemon695/RSSNest/issues)
- Email: lemon695@example.com

---

<div align="center">

**如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！**

Made with ❤️ by [Lemon695](https://github.com/Lemon695)

</div>





