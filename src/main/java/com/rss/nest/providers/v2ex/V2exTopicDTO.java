package com.rss.nest.providers.v2ex;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * V2EX主题数据传输对象
 * 映射V2EX API返回的JSON结构
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class V2exTopicDTO {

    /**
     * 主题ID
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * URL
     */
    private String url;

    /**
     * 内容（纯文本）
     */
    private String content;

    /**
     * 渲染后的内容（HTML）
     */
    @JsonProperty("content_rendered")
    private String contentRendered;

    /**
     * 回复数
     */
    private Integer replies;

    /**
     * 创建时间（Unix时间戳，秒）
     */
    private Long created;

    /**
     * 最后修改时间
     */
    @JsonProperty("last_modified")
    private Long lastModified;

    /**
     * 最后回复时间
     */
    @JsonProperty("last_touched")
    private Long lastTouched;

    /**
     * 成员信息
     */
    private Member member;

    /**
     * 节点信息
     */
    private Node node;

    /**
     * 成员信息内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Member {
        /**
         * 用户名
         */
        private String username;

        /**
         * ID
         */
        private Long id;

        /**
         * 头像URL
         */
        @JsonProperty("avatar_normal")
        private String avatarNormal;

        /**
         * 个人主页
         */
        private String url;
    }

    /**
     * 节点信息内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Node {
        /**
         * 节点ID
         */
        private Long id;

        /**
         * 节点名称
         */
        private String name;

        /**
         * 节点标题
         */
        private String title;

        /**
         * 节点URL
         */
        private String url;
    }
}
