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

    /**
     * 解析时间（支持字符串格式）
     *
     * @param format  时间格式字符串 如：yyyy-MM-dd HH:mm:ss
     * @param dateStr 时间字符串
     * @return Date对象
     */
    public static Date parseDateStr(String format, String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error("日期解析失败, format={}, dateStr={}, error={}", format, dateStr, e.toString());
            return null;
        }
    }

    /**
     * 自动解析日期（尝试多种常见格式）
     *
     * @param dateStr 时间字符串
     * @return Date对象
     */
    public static Date parseDate(String dateStr) {
        String[] formats = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd",
                "yyyy年MM月dd日 HH:mm:ss",
                "yyyy年MM月dd日",
                "MM-dd HH:mm",
                "MM/dd HH:mm"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                return sdf.parse(dateStr);
            } catch (ParseException e) {
                // 继续尝试下一个格式
            }
        }

        log.warn("无法解析日期: {}", dateStr);
        return null;
    }

}
