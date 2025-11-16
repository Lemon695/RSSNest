package com.rss.nest.core.exception;

/**
 * RSS服务基础异常类
 * 所有RSS相关异常的父类
 */
public class RssException extends RuntimeException {

    private String errorCode;

    public RssException(String message) {
        super(message);
    }

    public RssException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RssException(String message, Throwable cause) {
        super(message, cause);
    }

    public RssException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
