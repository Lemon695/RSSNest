package com.rss.nest.providers.weibo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 微博热搜数据传输对象
 * 映射微博API返回的JSON结构
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeiboHotSearchDTO {

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
         * 卡片列表
         */
        private List<Card> cards;
    }

    /**
     * 卡片内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        /**
         * 卡片类型
         */
        @JsonProperty("card_type")
        private Integer cardType;

        /**
         * 卡片组
         */
        @JsonProperty("card_group")
        private List<CardGroup> cardGroup;
    }

    /**
     * 卡片组内部类
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CardGroup {
        /**
         * 描述
         */
        private String desc;

        /**
         * 描述外部链接
         */
        @JsonProperty("desc_extr")
        private String descExtr;

        /**
         * 话题标题
         */
        @JsonProperty("title_sub")
        private String titleSub;

        /**
         * 跳转URL
         */
        private String scheme;

        /**
         * 热度值
         */
        @JsonProperty("icon_desc")
        private String iconDesc;

        /**
         * 热度数值
         */
        @JsonProperty("icon_desc_color")
        private String iconDescColor;

        /**
         * 排名
         */
        private Integer rank;
    }
}
