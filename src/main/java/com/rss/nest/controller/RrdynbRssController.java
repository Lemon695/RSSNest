package com.rss.nest.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.rometools.rome.feed.rss.Channel;
import com.rss.nest.function.rrdynb.core.IRrdynbRssApiCoreLogicService;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.utils.rss.RssOutPutUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:21:16
 * @description:
 */
@Slf4j
@ApiSupport(order = 2)
@Tag(name = "人人电影网")
@RestController
@RequestMapping("/rss/rrdynb")
public class RrdynbRssController {

    @Resource
    private IRrdynbRssApiCoreLogicService rrdynbRssApiCoreLogicService;

    @ApiOperationSupport(order = 1)
    @Operation(summary = "人人电影网-最新视频列表")
    @GetMapping(path = "/newVideo/{category}")
    public Channel newVideoList(@RequestHeader HttpHeaders headers, @PathVariable("category") String category) {

        try {
            RssChannel rssChannel = rrdynbRssApiCoreLogicService.newVideoList(category);
            return RssOutPutUtil.rssOutPut(rssChannel);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
