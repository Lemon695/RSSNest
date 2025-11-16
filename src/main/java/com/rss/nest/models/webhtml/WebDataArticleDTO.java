package com.rss.nest.models.webhtml;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:33:16
 * @description: 通用解析Web网页数据——列数据
 */
@Data
public class WebDataArticleDTO {

    /**
     * 标题
     */
    private String title;

    /**
     * 链接
     */
    private String url;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 作者
     */
    private String author;

    /**
     * 分类
     */
    private String category;

    /**
     * 自定义字段
     */
    private Map<String, String> customFields;

    public WebDataArticleDTO() {
    }

    public WebDataArticleDTO(String title, String url, String content, Date publishTime) {
        this.title = title;
        this.url = url;
        this.content = content;
        this.publishTime = publishTime;
    }

    /**
     * 添加自定义字段
     */
    public void addCustomField(String key, String value) {
        if (customFields == null) {
            customFields = new HashMap<>();
        }
        customFields.put(key, value);
    }

    /**
     * 获取自定义字段
     */
    public String getCustomField(String key) {
        if (customFields == null) {
            return null;
        }
        return customFields.get(key);
    }
}
