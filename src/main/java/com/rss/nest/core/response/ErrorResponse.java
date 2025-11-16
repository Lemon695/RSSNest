package com.rss.nest.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 错误响应对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * 错误码
     */
    private String errorCode;

    /**
     * 错误消息
     */
    private String message;

    /**
     * 详细信息
     */
    private String details;

    /**
     * 时间戳
     */
    private LocalDateTime timestamp;

    /**
     * 请求路径
     */
    private String path;

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String errorCode, String message, String details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }
}
