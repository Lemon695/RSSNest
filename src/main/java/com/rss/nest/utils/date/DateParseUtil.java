package com.rss.nest.utils.date;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:46:16
 * @description: 解析时间
 */
@Slf4j
public class DateParseUtil {

    public static final SimpleDateFormat SDF_V1_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");


    /**
     * 解析时间
     *
     * @param simpleDateFormat 时间格式封装
     * @param dateStr          时间字符串
     * @return: java.util.Date
     * @author Lemon695
     * @date 2024/9/30 下午4:48
     */
    public static Date parseDateStr(SimpleDateFormat simpleDateFormat, String dateStr) {
        Date date = null;
        try {
            date = simpleDateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.error("ParseException={}", e.toString());
        }

        return date;
    }

}
