package com.rss.nest.utils.xml;

/**
 * @author Lemon695
 * @description: 清理XML非法字符
 * @date: 2024/7/21 下午6:23
 * @modified By:
 */
public class XmlCharCleaner {

    public static String sanitizeXmlString(String input) {
        StringBuilder sanitized = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (isValidXmlChar(ch)) {
                sanitized.append(ch);
            } else {
                sanitized.append(replaceInvalidChar(ch));
            }
        }
        return sanitized.toString();
    }

    private static boolean isValidXmlChar(char ch) {
        return (ch == 0x9 || ch == 0xA || ch == 0xD ||
                (ch >= 0x20 && ch <= 0xD7FF) ||
                (ch >= 0xE000 && ch <= 0xFFFD) ||
                (ch >= 0x10000 && ch <= 0x10FFFF));
    }

    private static String replaceInvalidChar(char ch) {
        // 替换为有效的占位符或空字符串
        return "";
    }

}
