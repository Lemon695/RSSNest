package com.rss.nest.core.exception;

/**
 * 不支持的网站异常
 */
public class UnsupportedSiteException extends RssException {

    public UnsupportedSiteException(String siteId) {
        super("SITE_NOT_SUPPORTED", "不支持的网站: " + siteId);
    }

    public UnsupportedSiteException(String siteId, Throwable cause) {
        super("SITE_NOT_SUPPORTED", "不支持的网站: " + siteId, cause);
    }
}
