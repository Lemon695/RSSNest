package com.rss.nest.providers.v2ex;

import lombok.Getter;

/**
 * V2EX分类枚举
 * 对应V2EX API的不同主题类型
 */
@Getter
public enum V2exCategoryEnum {

    /**
     * 最热主题
     */
    HOT("hot", "最热主题", "https://www.v2ex.com/api/topics/hot.json"),

    /**
     * 最新主题
     */
    LATEST("latest", "最新主题", "https://www.v2ex.com/api/topics/latest.json");

    /**
     * 分类标识
     */
    private final String code;

    /**
     * 分类名称
     */
    private final String name;

    /**
     * API地址
     */
    private final String apiUrl;

    V2exCategoryEnum(String code, String name, String apiUrl) {
        this.code = code;
        this.name = name;
        this.apiUrl = apiUrl;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 分类代码
     * @return 对应的枚举，如果不存在返回null
     */
    public static V2exCategoryEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (V2exCategoryEnum category : values()) {
            if (category.code.equalsIgnoreCase(code)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 检查代码是否有效
     *
     * @param code 分类代码
     * @return 是否有效
     */
    public static boolean isValidCode(String code) {
        return getByCode(code) != null;
    }
}
