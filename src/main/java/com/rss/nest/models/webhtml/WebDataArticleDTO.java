package com.rss.nest.models.webhtml;

import lombok.Data;

import java.util.Date;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:33:16
 * @description: 通用解析Web网页数据——列数据
 */
@Data
public class WebDataArticleDTO {

    private String title;

    private String url;

    private String content;

    /**
     * 发布时间
     */
    private Date publishTime;

    public WebDataArticleDTO() {
    }

    public WebDataArticleDTO(String title, String url, String content, Date publishTime) {
        this.title = title;
        this.url = url;
        this.content = content;
        this.publishTime = publishTime;
    }
}
