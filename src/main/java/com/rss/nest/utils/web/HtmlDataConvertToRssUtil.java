package com.rss.nest.utils.web;

import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
import com.rss.nest.utils.rss.RssChannelDataUtil;
import com.rss.nest.utils.rss.RssSetUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午6:00:18
 * @description:
 */
public class HtmlDataConvertToRssUtil {

    /**
     * 解析数据转为RSS
     *
     * @param data HTML数据
     * @return: com.rss.nest.models.rss.RssChannel
     * @author Lemon695
     * @date 2024/9/30 下午6:18
     */
    public static RssChannel analysisDocDataToRss(WebHtmlDataDTO data) {
        String title = data.getTitle();
        String url = data.getLink();
        String description = data.getDescription();
        RssChannel rssChannel = RssChannelDataUtil.setRssChannel(title, url, description);

        //N条数据
        List<WebDataArticleDTO> articleList = data.getArticleList();
        for (WebDataArticleDTO item : articleList) {
            String itemTitle = item.getTitle();
            String itemLink = item.getUrl();
            String itemDescription = item.getContent();
            if (StringUtils.isBlank(itemDescription)) {
                itemDescription = itemTitle;
            }

            Date itemDate = item.getPublishTime();
            if (itemDate == null) {
                itemDate = new Date();
            }

            RssSetUtil.setRssChannelData(itemTitle, itemLink, itemDate, itemDescription, rssChannel);
        }

        return rssChannel;
    }
}
