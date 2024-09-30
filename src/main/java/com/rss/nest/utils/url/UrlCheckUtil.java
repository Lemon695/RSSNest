package com.rss.nest.utils.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:28:23
 * @description:
 */
public class UrlCheckUtil {

    /**
     * 根据regexStr校验URI链接是否复合
     *
     * @param regexStr 正则校验
     * @param uri      URI链接
     * @return boolean
     * @author Lemon695
     * @date 2024/07/16 11:53
     */
    public static boolean checkUrlRegex(String regexStr, String uri) {
        Pattern pattern = Pattern.compile(regexStr);
        Matcher matcher = pattern.matcher(uri);
        return matcher.matches();
    }
}
