package com.rss.nest.controller;

import com.rss.nest.core.cache.CacheableRssService;
import com.rss.nest.core.provider.RssProviderFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统一RSS控制器
 * 提供统一的RSS生成接口
 */
@Slf4j
@RestController
@RequestMapping("/api/rss")
@Tag(name = "统一RSS接口", description = "新架构的统一RSS生成接口")
public class UnifiedRssController {

    @Autowired
    private CacheableRssService cacheableRssService;

    @Autowired
    private RssProviderFactory providerFactory;

    /**
     * 统一RSS生成接口
     */
    @GetMapping(value = "/{siteId}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "生成RSS", description = "根据网站ID和参数生成RSS订阅源")
    public String generateRss(
            @Parameter(description = "网站ID，如: rrdynb") @PathVariable String siteId,
            @Parameter(description = "请求参数，如: category=movie") @RequestParam(required = false) Map<String, String> params) {

        log.info("收到RSS生成请求, siteId: {}, params: {}", siteId, params);

        if (params == null) {
            params = new HashMap<>();
        }

        return cacheableRssService.generateRssWithCache(siteId, params);
    }

    /**
     * 带分类的RSS生成接口（便捷方法）
     */
    @GetMapping(value = "/{siteId}/{category}", produces = MediaType.APPLICATION_XML_VALUE)
    @Operation(summary = "生成RSS（带分类）", description = "根据网站ID和分类生成RSS订阅源")
    public String generateRssWithCategory(
            @Parameter(description = "网站ID") @PathVariable String siteId,
            @Parameter(description = "分类") @PathVariable String category,
            @Parameter(description = "其他参数") @RequestParam(required = false) Map<String, String> params) {

        log.info("收到RSS生成请求, siteId: {}, category: {}, params: {}", siteId, category, params);

        if (params == null) {
            params = new HashMap<>();
        }
        params.put("category", category);

        return cacheableRssService.generateRssWithCache(siteId, params);
    }

    /**
     * 清除缓存接口
     */
    @DeleteMapping("/{siteId}/cache")
    @Operation(summary = "清除缓存", description = "清除指定网站的RSS缓存")
    public Map<String, Object> clearCache(
            @Parameter(description = "网站ID") @PathVariable String siteId,
            @Parameter(description = "请求参数") @RequestParam(required = false) Map<String, String> params) {

        log.info("清除缓存, siteId: {}, params: {}", siteId, params);

        if (params == null || params.isEmpty()) {
            // 清空整个网站的缓存
            cacheableRssService.clearSiteCache(siteId);
            return Map.of("success", true, "message", "已清空网站所有缓存");
        } else {
            // 清除特定参数的缓存
            cacheableRssService.clearCache(siteId, params);
            return Map.of("success", true, "message", "已清除指定缓存");
        }
    }

    /**
     * 获取支持的网站列表
     */
    @GetMapping("/sites")
    @Operation(summary = "获取支持的网站", description = "获取所有支持的网站列表")
    public Map<String, Object> getSupportedSites() {
        List<String> sites = providerFactory.getSupportedSites();
        return Map.of(
                "total", sites.size(),
                "sites", sites
        );
    }

    /**
     * 获取网站详细信息
     */
    @GetMapping("/sites/info")
    @Operation(summary = "获取网站详情", description = "获取所有支持的网站详细信息")
    public Map<String, Object> getSitesInfo() {
        var providersInfo = providerFactory.getAllProvidersInfo();
        return Map.of(
                "total", providersInfo.size(),
                "providers", providersInfo
        );
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查RSS服务是否正常")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "RSSNest",
                "version", "2.0",
                "supportedSites", providerFactory.getSupportedSites().size()
        );
    }
}
