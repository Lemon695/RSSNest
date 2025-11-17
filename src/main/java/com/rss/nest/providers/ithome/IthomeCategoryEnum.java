package com.rss.nest.providers.ithome;

import lombok.Getter;

/**
 * IT之家分类枚举
 */
@Getter
public enum IthomeCategoryEnum {

    /**
     * IT资讯
     */
    IT("it", "IT资讯"),

    /**
     * 软件之家
     */
    SOFT("soft", "软件之家"),

    /**
     * Win10之家
     */
    WIN10("win10", "Win10之家"),

    /**
     * Win11之家
     */
    WIN11("win11", "Win11之家"),

    /**
     * iPhone之家
     */
    IPHONE("iphone", "iPhone之家"),

    /**
     * iPad之家
     */
    IPAD("ipad", "iPad之家"),

    /**
     * Android之家
     */
    ANDROID("android", "Android之家"),

    /**
     * 数码之家
     */
    DIGI("digi", "数码之家"),

    /**
     * 下一代
     */
    NEXT("next", "下一代");

    /**
     * 分类代码
     */
    private final String code;

    /**
     * 分类名称
     */
    private final String name;

    IthomeCategoryEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 分类代码
     * @return 对应的枚举，如果不存在返回null
     */
    public static IthomeCategoryEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (IthomeCategoryEnum category : values()) {
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

    /**
     * 获取URL
     *
     * @return URL
     */
    public String getUrl() {
        return "https://" + code + ".ithome.com/";
    }
}
