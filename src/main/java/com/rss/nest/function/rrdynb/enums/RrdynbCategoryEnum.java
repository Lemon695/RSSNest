package com.rss.nest.function.rrdynb.enums;

import com.rss.nest.enums.EnumResultCode;
import com.rss.nest.exception.RSSNestException;
import lombok.Getter;

import java.util.Objects;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:55:16
 * @description: 人人影视网-枚举
 */
@Getter
public enum RrdynbCategoryEnum {

    MOVIE("movie", "电影"),
    DIAN_SHI_JU("dianshiju", "电视剧"),
    ZONG_YI("zongyi", "老电影"),
    DONG_MAN("dongman", "动漫"),

    ;
    /**
     * 分类
     */
    private final String category;

    /**
     * 名称
     */
    private final String name;

    RrdynbCategoryEnum(final String category, final String name) {
        this.category = category;
        this.name = name;
    }

    /**
     * 根据分类匹配枚举
     *
     * @param category 分类
     * @return: com.rss.nest.function.rrdynb.enums.RrdynbCategoryEnum
     * @author Lemon695
     * @date 2024/9/30 下午6:14
     */
    public static RrdynbCategoryEnum getEnumDataByDataType(String category) {
        for (RrdynbCategoryEnum data : RrdynbCategoryEnum.values()) {
            if (Objects.equals(category, data.getCategory())) {
                return data;
            }
        }
        return null;
    }

    /**
     * 判断枚举为空
     *
     * @param categoryEnum 分类枚举
     * @return: void
     * @author Lemon695
     * @date 2024/9/30 下午6:13
     */
    public static void checkRrdynbCategoryEnumIsNull(RrdynbCategoryEnum categoryEnum) throws RSSNestException {
        if (categoryEnum == null) {
            throw new RSSNestException(EnumResultCode.EXCEPTION);
        }
    }
}
