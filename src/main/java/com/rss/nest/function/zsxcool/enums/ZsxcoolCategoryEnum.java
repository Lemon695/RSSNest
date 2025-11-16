package com.rss.nest.function.zsxcool.enums;

import com.rss.nest.function.rrdynb.enums.RrdynbCategoryEnum;
import lombok.Getter;

import java.util.Objects;

/**
 * @author Lemon695
 * @date: 2024/10/11 下午3:18:15
 * @description: 资源分类
 */
@Getter
public enum ZsxcoolCategoryEnum {

    MOVIE("movie", "影视资源","https://www.zsxcool.com/tag/%e5%bd%b1%e8%a7%86%e8%b5%84%e6%ba%90"),

    ;
    /**
     * 分类
     */
    private final String category;

    /**
     * 名称
     */
    private final String name;

    /**
     * URL链接
     */
    private final String url;

    ZsxcoolCategoryEnum(final String category, final String name,String url) {
        this.category = category;
        this.name = name;
        this.url = url;
    }

    /**
     * 根据分类匹配枚举
     *
     * @param category 分类
     * @return: 枚举分类
     * @author Lemon695
     * @date 2024/10/11 下午3:14
     */
    public static ZsxcoolCategoryEnum getEnumDataByDataType(String category) {
        for (ZsxcoolCategoryEnum data : ZsxcoolCategoryEnum.values()) {
            if (Objects.equals(category, data.getCategory())) {
                return data;
            }
        }
        return null;
    }
}
