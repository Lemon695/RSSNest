package com.rss.nest.core.provider;

import com.rss.nest.core.exception.UnsupportedSiteException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RSS提供者工厂测试
 */
@SpringBootTest
class RssProviderFactoryTest {

    @Autowired
    private RssProviderFactory providerFactory;

    @Test
    void testGetProvider() {
        // 测试获取已注册的Provider
        RssProviderService provider = providerFactory.getProvider("rrdynb");
        assertNotNull(provider);
        assertEquals("rrdynb", provider.getSiteIdentifier());
    }

    @Test
    void testGetUnsupportedProvider() {
        // 测试获取不存在的Provider
        assertThrows(UnsupportedSiteException.class, () -> {
            providerFactory.getProvider("nonexistent");
        });
    }

    @Test
    void testIsSupported() {
        assertTrue(providerFactory.isSupported("rrdynb"));
        assertFalse(providerFactory.isSupported("nonexistent"));
    }

    @Test
    void testGetSupportedSites() {
        List<String> sites = providerFactory.getSupportedSites();
        assertNotNull(sites);
        assertTrue(sites.size() > 0);
        assertTrue(sites.contains("rrdynb"));
    }

    @Test
    void testGetAllProvidersInfo() {
        var providersInfo = providerFactory.getAllProvidersInfo();
        assertNotNull(providersInfo);
        assertTrue(providersInfo.size() > 0);

        var info = providersInfo.get(0);
        assertNotNull(info.getSiteId());
        assertNotNull(info.getSiteName());
    }
}
