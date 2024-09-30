package com.rss.nest.query;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:23:23
 * @description:
 */
public interface IRssCacheDataQueryService {

    /**
     * 解析获得请求的RssRedisKey
     *
     * @param request 请求Request
     * @return java.lang.String
     * @author Lemon695
     * @date 2024/07/16 11:06
     */
    String createRssRedisKey(HttpServletRequest request) throws Exception;
}
