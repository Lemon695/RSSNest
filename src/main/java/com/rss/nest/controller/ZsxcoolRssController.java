package com.rss.nest.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.rometools.rome.feed.rss.Channel;
import com.rss.nest.function.rrdynb.core.IRrdynbRssApiCoreLogicService;
import com.rss.nest.function.zsxcool.core.IZsxcoolRssApiCoreLogicService;
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
 * @date: 2024/10/11 下午3:06:15
 * @description:
 */
@Slf4j
@ApiSupport(order = 3)
@Tag(name = "字节制造")
@RestController
@RequestMapping("/rss/zsxcool")
public class ZsxcoolRssController {

    @Resource
    private IZsxcoolRssApiCoreLogicService zsxcoolRssApiCoreLogicService;

    @ApiOperationSupport(order = 1)
    @Operation(summary = "字节制造-最新资源列表")
    @GetMapping(path = "/resource/{category}")
    public Channel categoryResource(@RequestHeader HttpHeaders headers, @PathVariable("category") String category) {

        try {
            RssChannel rssChannel = zsxcoolRssApiCoreLogicService.categoryResourceFeed(category);
            return RssOutPutUtil.rssOutPut(rssChannel);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
