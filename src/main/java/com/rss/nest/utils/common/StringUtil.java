package com.rss.nest.utils.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午6:05:18
 * @description:
 */
@Slf4j
public class StringUtil {

    public static String getEncodeText(String text) {
        String encodedText = "";
        if (StringUtils.isNotBlank(text)) {
            try {
                encodedText = new String(text.getBytes(), "UTF-8");
            } catch (Exception e) {
                if (e instanceof UnsupportedEncodingException) {
                    log.error("getEncodeText", "UnsupportedEncodingException=" + e.toString());
                } else {
                    log.error("getEncodeText", "Exception=" + e.toString());
                }
            }
        }

        return encodedText;
    }
}
