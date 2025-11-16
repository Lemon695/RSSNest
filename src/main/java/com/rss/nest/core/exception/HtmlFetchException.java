package com.rss.nest.core.exception;

/**
 * HTML抓取异常
 */
public class HtmlFetchException extends RssException {

    public HtmlFetchException(String message) {
        super("HTML_FETCH_FAILED", message);
    }

    public HtmlFetchException(String message, Throwable cause) {
        super("HTML_FETCH_FAILED", message, cause);
    }
}
