package com.rss.nest.models.webhtml;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:33:16
 * @description: 通用解析Web网页数据
 */
@Data
public class WebHtmlDataDTO {

    private String title;
    private String description;
    private String link;
    private List<WebDataArticleDTO> articleList = new ArrayList<>(128);

    public WebHtmlDataDTO() {
    }

    public WebHtmlDataDTO(String title, String description, String link, List<WebDataArticleDTO> articleList) {
        this.title = title;
        this.description = description;
        this.link = link;
        this.articleList = articleList;
    }
}
