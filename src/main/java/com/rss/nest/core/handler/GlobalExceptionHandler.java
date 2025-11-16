package com.rss.nest.core.handler;

import com.rss.nest.core.exception.*;
import com.rss.nest.core.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

/**
 * 全局异常处理器
 * 统一处理所有异常并返回友好的错误信息
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理不支持的网站异常
     */
    @ExceptionHandler(UnsupportedSiteException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedSite(
            UnsupportedSiteException ex, WebRequest request) {
        log.warn("不支持的网站: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                "请检查网站ID是否正确，或查看支持的网站列表"
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * 处理HTML抓取异常
     */
    @ExceptionHandler(HtmlFetchException.class)
    public ResponseEntity<ErrorResponse> handleHtmlFetch(
            HtmlFetchException ex, WebRequest request) {
        log.error("HTML抓取失败: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                "无法访问目标网站",
                ex.getMessage()
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    /**
     * 处理HTML解析异常
     */
    @ExceptionHandler(HtmlParseException.class)
    public ResponseEntity<ErrorResponse> handleHtmlParse(
            HtmlParseException ex, WebRequest request) {
        log.error("HTML解析失败: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                "数据解析失败",
                "网站结构可能已变化，请联系管理员更新解析规则"
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 处理RSS生成异常
     */
    @ExceptionHandler(RssGenerationException.class)
    public ResponseEntity<ErrorResponse> handleRssGeneration(
            RssGenerationException ex, WebRequest request) {
        log.error("RSS生成失败: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode(),
                "RSS生成失败",
                ex.getMessage()
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 处理参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("参数错误: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "INVALID_PARAMETER",
                "请求参数错误",
                ex.getMessage()
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 处理RSS基础异常
     */
    @ExceptionHandler(RssException.class)
    public ResponseEntity<ErrorResponse> handleRssException(
            RssException ex, WebRequest request) {
        log.error("RSS异常: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                ex.getErrorCode() != null ? ex.getErrorCode() : "RSS_ERROR",
                "RSS服务异常",
                ex.getMessage()
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, WebRequest request) {
        log.error("未处理的异常: {}", ex.getMessage(), ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "服务器内部错误",
                "请稍后重试或联系管理员"
        );
        error.setPath(getRequestPath(request));
        error.setTimestamp(LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 获取请求路径
     */
    private String getRequestPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
