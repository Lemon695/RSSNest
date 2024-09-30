package com.rss.nest.utils;

/**
 * @author Lemon695
 * @date: 2024/10/1 上午12:18:00
 * @description: 日志处理
 */
public class LogUtil {

    /**
     * 封装日志
     *
     * @param tag     标记
     * @param message 日志内容
     * @return: java.lang.String
     * @author Lemon695
     * @date 2024/10/1 上午12:21
     */
    public static String getTagInfo(String tag, String message) {
        StringBuilder builder = new StringBuilder();
        builder.append(tag)
                .append("[")
                .append(message)
                .append("]");
        return builder.toString();
    }

}
