package com.rss.nest.models.rss;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:28:16
 * @description: 解析得到的N条数据
 */
@Data
public class RssItem {

    private String title;

    private String description;

    private Date pubDate;

    /**
     * 字符串格式的“发布日期”
     */
    private String pubDateStr;

    private String link;

    private String guid;

    private String uri;

    private String comments;
    /**
     * 类型
     */
    private String category;
    /**
     * dc:creator
     */
    private String creator;

    private String author;
    /**
     * 部分RSS中，description是文章缩略前言，content:encoded是全文
     */
    private String descriptionContent;

    private List<RssModule> modules;

    private RssContent content;

    private RssSource source;

    private Date expirationDate;

}
