package com.rss.nest.core.config;

import lombok.Data;
import java.util.Map;

/**
 * HTML解析配置类
 * 基于CSS选择器的配置化解析
 */
@Data
public class ParseConfig {

    /**
     * 列表容器选择器（必填）
     */
    private String listSelector;

    /**
     * 标题选择器（必填）
     */
    private String titleSelector;

    /**
     * 链接选择器（必填）
     */
    private String linkSelector;

    /**
     * 链接属性名（默认为href）
     */
    private String linkAttribute = "href";

    /**
     * 内容选择器
     */
    private String contentSelector;

    /**
     * 日期选择器
     */
    private String dateSelector;

    /**
     * 日期格式（如：yyyy-MM-dd HH:mm:ss）
     */
    private String dateFormat;

    /**
     * 图片选择器
     */
    private String imageSelector;

    /**
     * 图片属性名（默认为src，可能是data-src等）
     */
    private String imageAttribute = "src";

    /**
     * 作者选择器
     */
    private String authorSelector;

    /**
     * 分类选择器
     */
    private String categorySelector;

    /**
     * 自定义字段选择器
     * key: 字段名, value: 选择器
     */
    private Map<String, String> customSelectors;

    /**
     * 内容提取模式（text: 纯文本, html: HTML内容）
     */
    private String contentMode = "html";

    /**
     * 是否需要完整URL（相对路径转绝对路径）
     */
    private Boolean needFullUrl = true;

    /**
     * URL前缀（用于拼接相对路径）
     */
    private String urlPrefix;
}
