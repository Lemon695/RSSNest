package com.rss.nest.function.rrdynb.core.impl;

import com.rss.nest.function.rrdynb.core.IRrdynbRssApiCoreLogicService;
import com.rss.nest.function.rrdynb.enums.RrdynbCategoryEnum;
import com.rss.nest.function.rrdynb.models.RrdynbHtmlVideoDTO;
import com.rss.nest.function.rrdynb.utils.RrdynbDataConvertToRssUtil;
import com.rss.nest.function.rrdynb.utils.RrdynbParseHtmlUtil;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
import com.rss.nest.utils.file.FileReadUtil;
import com.rss.nest.utils.http.OkHttpClientUtil;
import com.rss.nest.utils.web.HtmlDataConvertToRssUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:27:16
 * @description: 人人影视-接口逻辑
 */
@Slf4j
@Service
public class RrdynbRssApiCoreLogicServiceImpl implements IRrdynbRssApiCoreLogicService {

    @Resource
    private OkHttpClientUtil okHttpClientUtil;

    /**
     * (人人电影网-最新视频列表)
     *
     * @param category 分类
     * @return: com.rss.nest.models.rss.RssChannel
     * @author Lemon695
     * @date 2024/9/30 下午6:10
     */
    @Override
    public RssChannel newVideoList(String category) {
        RrdynbCategoryEnum rrdynbCategoryEnum = RrdynbCategoryEnum.getEnumDataByDataType(category);
        if (rrdynbCategoryEnum == null) {
            return null;
        }

        String movieTitle = rrdynbCategoryEnum.getName() + " - 人人影视网";
        String link = String.format("https://www.rrdynb.com/%s/", category);

        Headers headers = new Headers.Builder().build();
        String result = okHttpClientUtil.doGet(link, headers);
        Document document = Jsoup.parse(result);

        List<RrdynbHtmlVideoDTO> videoList = RrdynbParseHtmlUtil.parseHtmlToVideoList(document);
        WebHtmlDataDTO webHtmlDataDTO = RrdynbDataConvertToRssUtil.checkParseVideoListToRss(movieTitle, link, videoList);

        //解析数据转为RSS
        return HtmlDataConvertToRssUtil.analysisDocDataToRss(webHtmlDataDTO);
    }
}
