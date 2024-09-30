package com.rss.nest.query.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rss.nest.common.constant.ConstantValue;
import com.rss.nest.query.IRssCacheDataQueryService;
import com.rss.nest.utils.JsonUtil;
import com.rss.nest.utils.rss.RssRedisKeyUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:24:23
 * @description:
 */
@Slf4j
@Service
public class RssCacheDataQueryServiceImpl implements IRssCacheDataQueryService {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 解析获得请求的RssRedisKey
     *
     * @param request 请求Request
     * @return java.lang.String
     * @author Lemon695
     * @date 2024/07/16 11:06
     */
    @Override
    public String createRssRedisKey(HttpServletRequest request) throws Exception {
        if (!StringUtils.equalsIgnoreCase(request.getMethod(), ConstantValue.HTTP_GET)) {
            return "";
        }

        String uri = request.getRequestURI();
        String paramStr = request.getRequestURI();

        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("uri--->" + uri);

        if (parameterMap.isEmpty()) {
            paramStr += IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        } else {
            paramStr += MAPPER.writeValueAsString(request.getParameterMap());
        }

        if (!parameterMap.isEmpty()) {
            System.out.println("parameterMap---" + JsonUtil.toJson(parameterMap, false));
        }

        return RssRedisKeyUtil.getRssRedisKey(uri, paramStr);
    }
}
