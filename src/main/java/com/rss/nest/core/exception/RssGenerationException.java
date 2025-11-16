package com.rss.nest.core.exception;

/**
 * RSS生成异常
 */
public class RssGenerationException extends RssException {

    public RssGenerationException(String message) {
        super("RSS_GENERATION_FAILED", message);
    }

    public RssGenerationException(String message, Throwable cause) {
        super("RSS_GENERATION_FAILED", message, cause);
    }
}
