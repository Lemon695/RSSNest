package com.rss.nest.utils.rss;

import com.rss.nest.common.constant.RssConstant;
import com.rss.nest.enums.UriWebNameEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:26:23
 * @description:
 */
@Slf4j
public class RssRedisKeyUtil {

    public static String getRssRedisKey(String uri, String paramStr) {
        String rssDataType = "rss";
        UriWebNameEnum uriWebNameEnum = UriWebNameEnum.getWebNameByUri(uri);
        if (uriWebNameEnum != null) {
            rssDataType = uriWebNameEnum.getWebName();
        }

        String rssRedisKey = String.format(RssConstant.RSS_NEST_CACHE_DATA, rssDataType, paramStr, DigestUtils.md5Hex(paramStr));
        log.info(String.format("rssRedisKey---%s", rssRedisKey));
        return rssRedisKey;
    }
}
