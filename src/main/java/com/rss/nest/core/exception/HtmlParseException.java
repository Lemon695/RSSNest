package com.rss.nest.core.exception;

/**
 * HTML解析异常
 */
public class HtmlParseException extends RssException {

    public HtmlParseException(String message) {
        super("HTML_PARSE_FAILED", message);
    }

    public HtmlParseException(String message, Throwable cause) {
        super("HTML_PARSE_FAILED", message, cause);
    }
}
