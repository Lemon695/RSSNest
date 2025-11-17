package com.rss.nest.providers.sspai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 少数派文章数据传输对象
 * 映射少数派API返回的JSON结构
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SspaiArticleDTO {

    /**
     * 响应数据
     */
    private ResponseData data;

    /**
     * 响应数据内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseData {
        /**
         * 文章列表
         */
        private List<Article> list;
    }

    /**
     * 文章内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Article {
        /**
         * 文章ID
         */
        private Long id;

        /**
         * 文章标题
         */
        private String title;

        /**
         * URL别名
         */
        private String slug;

        /**
         * 摘要
         */
        private String summary;

        /**
         * 创建时间（秒级时间戳）
         */
        @JsonProperty("created_at")
        private Long createdAt;

        /**
         * 发布时间（秒级时间戳）
         */
        @JsonProperty("released_at")
        private Long releasedAt;

        /**
         * 作者信息
         */
        private Author author;

        /**
         * 宣传图
         */
        private String banner;

        /**
         * 是否为会员文章
         */
        @JsonProperty("member")
        private Boolean isMember;
    }

    /**
     * 作者内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Author {
        /**
         * 作者ID
         */
        private Long id;

        /**
         * 作者昵称
         */
        private String nickname;

        /**
         * 作者头像
         */
        private String avatar;
    }
}
