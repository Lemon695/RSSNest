package com.rss.nest.providers.github;

import lombok.Getter;

/**
 * GitHub Trending时间范围枚举
 */
@Getter
public enum GithubCategoryEnum {

    /**
     * 今日趋势
     */
    DAILY("daily", "今日趋势", "daily"),

    /**
     * 本周趋势
     */
    WEEKLY("weekly", "本周趋势", "weekly"),

    /**
     * 本月趋势
     */
    MONTHLY("monthly", "本月趋势", "monthly");

    /**
     * 分类代码
     */
    private final String code;

    /**
     * 分类名称
     */
    private final String name;

    /**
     * since参数值
     */
    private final String since;

    GithubCategoryEnum(String code, String name, String since) {
        this.code = code;
        this.name = name;
        this.since = since;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 分类代码
     * @return 对应的枚举，如果不存在返回null
     */
    public static GithubCategoryEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (GithubCategoryEnum category : values()) {
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
