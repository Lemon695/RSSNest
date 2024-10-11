package com.rss.nest.function.rrdynb.utils;

import com.rss.nest.function.rrdynb.models.RrdynbHtmlVideoDTO;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
import com.rss.nest.utils.JsonUtil;
import com.rss.nest.utils.date.DateParseUtil;

import java.util.Date;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:38:16
 * @description:
 */
public class RrdynbDataConvertToRssUtil {

    /**
     * 处理网页数据,转为可封装RSS的网页数据
     *
     * @param movieTitle RSS标题
     * @param link       RSS链接
     * @param videoList  待处理视频数据集合
     * @return: com.rss.nest.models.webhtml.WebHtmlDataDTO
     * @author Lemon695
     * @date 2024/9/30 下午6:21
     */
    public static WebHtmlDataDTO checkParseVideoListToRss(String movieTitle, String link, List<RrdynbHtmlVideoDTO> videoList) {
        WebHtmlDataDTO webHtmlDataDTO = new WebHtmlDataDTO();
        List<WebDataArticleDTO> articleList = webHtmlDataDTO.getArticleList();

        if (videoList.isEmpty()) {
            return webHtmlDataDTO;
        }

        for (RrdynbHtmlVideoDTO item : videoList) {
            String itemTitle = item.getTitle();
            String itemUrl = item.getUrl();
            String director = item.getDirector();
            String brief = item.getBrief();
            String videoDateStr = item.getDate();
            String imageUrl = item.getImageUrl();

            //时间
            Date createAtDate = DateParseUtil.parseDateStr(DateParseUtil.SDF_V1_YYYY_MM_DD, videoDateStr);
            if (createAtDate == null) {
                createAtDate = new Date();
            }

            StringBuilder itemContentBuilder = new StringBuilder();
            itemContentBuilder.append(itemTitle).append("<br>");
            itemContentBuilder.append(director).append("<br>");
            itemContentBuilder.append(brief).append("<br>");
            itemContentBuilder.append(String.format("<img src = '%s' >", imageUrl)).append("<br>");
            String itemContent = itemContentBuilder.toString();

            //封装数据
            WebDataArticleDTO article = new WebDataArticleDTO();
            article.setTitle(itemTitle);
            article.setUrl(itemUrl);
            article.setContent(itemContent);
            article.setPublishTime(createAtDate);
            articleList.add(article);
        }

        webHtmlDataDTO.setTitle(movieTitle);
        webHtmlDataDTO.setDescription("");
        webHtmlDataDTO.setLink(link);
        webHtmlDataDTO.setArticleList(articleList);
        return webHtmlDataDTO;
    }
}
