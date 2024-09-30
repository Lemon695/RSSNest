package com.rss.nest.common.constant;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:05:23
 * @description:
 */
public class Constants {

    /**
     * 自动识别json对象白名单配置（仅允许解析的包名，范围越小越安全）
     */
    public static final String[] JSON_WHITELIST_STR = {"org.springframework"};
}
