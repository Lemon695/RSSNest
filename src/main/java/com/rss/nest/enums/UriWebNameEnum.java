package com.rss.nest.enums;

import com.rss.nest.utils.url.UrlCheckUtil;
import lombok.Getter;

/**
 * @author Lemon695
 * @description: URL-对应缓存分组
 * @date: 2024/7/5 下午4:33
 */
@Getter
public enum UriWebNameEnum {

    ;

    private final String uriRegex;
    private final String webName;

    UriWebNameEnum(String uriRegex, String webName) {
        this.uriRegex = uriRegex;
        this.webName = webName;
    }

    /**
     * 根据URI获得WebName
     *
     * @param uri URI接口
     * @return java.lang.String
     * @author Lemon695
     * @date 2024/07/05 16:48
     */
    public static String getWebNameStrByUri(String uri) {
        UriWebNameEnum uriWebNameEnum = getWebNameByUri(uri);
        if (uriWebNameEnum != null) {
            return uriWebNameEnum.getWebName();
        }

        return null;
    }

    /**
     * 根据URI获得WebName
     *
     * @param uri URI接口
     * @return Uri枚举分类
     * @author Lemon695
     * @date 2024/07/05 16:51
     */
    public static UriWebNameEnum getWebNameByUri(String uri) {
        for (UriWebNameEnum data : UriWebNameEnum.values()) {
            boolean flag = checkUrlWebName(uri, data);
            if (flag) {
                return data;
            }
        }

        return null;
    }

    /**
     * 判断URI是否是接口,REGEX校验
     *
     * @param uri            URI接口
     * @param uriWebNameEnum URL枚举分组名称
     * @return boolean
     * @author Lemon695
     * @date 2024/07/05 16:06
     */
    public static boolean checkUrlWebName(String uri, UriWebNameEnum uriWebNameEnum) {
        String regexStr = uriWebNameEnum.getUriRegex();
        return UrlCheckUtil.checkUrlRegex(regexStr, uri);
    }

    public static void main(String[] args) {

    }
}
