package com.rss.nest.providers.sspai;

import lombok.Getter;

/**
 * 少数派分类枚举
 */
@Getter
public enum SspaiCategoryEnum {

    /**
     * 首页推荐
     */
    INDEX("index", "首页推荐", "https://sspai.com/api/v1/article/index/page/get"),

    /**
     * Matrix首页
     */
    MATRIX("matrix", "Matrix首页", "https://sspai.com/api/v1/article/matrix/page/get");

    /**
     * 分类代码
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

    SspaiCategoryEnum(String code, String name, String apiUrl) {
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
    public static SspaiCategoryEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (SspaiCategoryEnum category : values()) {
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
