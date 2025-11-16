package com.rss.nest.core.config;

import lombok.Data;

/**
 * RSS配置类
 */
@Data
public class RssConfig {

    /**
     * RSS标题
     */
    private String title;

    /**
     * RSS描述
     */
    private String description;

    /**
     * RSS链接
     */
    private String link;

    /**
     * RSS语言（默认zh-CN）
     */
    private String language = "zh-CN";

    /**
     * RSS生成器标识
     */
    private String generator = "RSSNest";

    /**
     * 版权信息
     */
    private String copyright;

    /**
     * 每页条目数量（默认20）
     */
    private Integer itemsPerPage = 20;

    /**
     * 是否包含内容（默认true）
     */
    private Boolean includeContent = true;

    /**
     * 是否包含图片（默认true）
     */
    private Boolean includeImage = true;
}
