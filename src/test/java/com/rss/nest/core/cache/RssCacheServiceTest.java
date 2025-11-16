package com.rss.nest.core.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RSS缓存服务测试
 */
@SpringBootTest
class RssCacheServiceTest {

    @Autowired
    private RssCacheService cacheService;

    private String siteId;
    private Map<String, String> params;

    @BeforeEach
    void setUp() {
        siteId = "test-site";
        params = new HashMap<>();
        params.put("category", "test");
    }

    @Test
    void testSetAndGet() {
        String testData = "<rss>test data</rss>";

        // 设置缓存
        cacheService.set(siteId, params, testData, 60L);

        // 获取缓存
        String cached = cacheService.get(siteId, params);
        assertEquals(testData, cached);
    }

    @Test
    void testDelete() {
        String testData = "<rss>test data</rss>";

        // 设置缓存
        cacheService.set(siteId, params, testData, 60L);

        // 删除缓存
        cacheService.delete(siteId, params);

        // 验证已删除
        String cached = cacheService.get(siteId, params);
        assertNull(cached);
    }

    @Test
    void testGetOrSet() {
        String testData = "<rss>generated data</rss>";

        // 第一次调用，应该执行supplier
        String result1 = cacheService.getOrSet(siteId, params, 60L, () -> testData);
        assertEquals(testData, result1);

        // 第二次调用，应该从缓存获取
        String result2 = cacheService.getOrSet(siteId, params, 60L, () -> {
            fail("不应该执行supplier");
            return null;
        });
        assertEquals(testData, result2);

        // 清理
        cacheService.delete(siteId, params);
    }

    @Test
    void testClearSite() {
        // 设置多个缓存
        Map<String, String> params1 = Map.of("category", "cat1");
        Map<String, String> params2 = Map.of("category", "cat2");

        cacheService.set(siteId, params1, "data1", 60L);
        cacheService.set(siteId, params2, "data2", 60L);

        // 清空网站缓存
        cacheService.clearSite(siteId);

        // 验证已清空
        assertNull(cacheService.get(siteId, params1));
        assertNull(cacheService.get(siteId, params2));
    }
}
