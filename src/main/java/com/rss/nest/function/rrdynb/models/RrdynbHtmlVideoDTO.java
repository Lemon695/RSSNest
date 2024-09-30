package com.rss.nest.function.rrdynb.models;

import lombok.Data;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:04:16
 * @description: 网页-视频信息
 */
@Data
public class RrdynbHtmlVideoDTO {

    private String title;

    private String url;
    /**
     * 导演
     */
    private String director;
    /**
     * 简介
     */
    private String brief;

    private String date;

    private String imageUrl;

    public RrdynbHtmlVideoDTO() {
    }

    public RrdynbHtmlVideoDTO(String title, String url, String director, String brief, String date, String imageUrl) {
        this.title = title;
        this.url = url;
        this.director = director;
        this.brief = brief;
        this.date = date;
        this.imageUrl = imageUrl;
    }
}
