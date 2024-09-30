package com.rss.nest.models.rss;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:28:16
 * @description: 解析得到的RSSChannel数据
 */
@Data
public class RssChannel {

    private String title;

    private String link;

    private String description;

    private String generator;

    private String webMaster;

    private String language;

    private Date lastBuildDate;

    private String lastBuildDate2;

    private String ttl;

    private List<RssItem> items;

    private String feedType;

    private String encoding;

    private String styleSheet;

    private List<RssModule> modules;

    public String uri;

    public Date pubDate;

    public RssImage image;


    public RssChannel() {
    }

    public RssChannel(String title, String link, String uri, String description, List<RssItem> items) {
        this.title = title;
        this.link = link;
        this.uri = uri;
        this.description = description;
        this.items = items;
    }
}
