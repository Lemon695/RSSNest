package com.rss.nest.function.zsxcool.utils;

import com.rss.nest.function.rrdynb.models.RrdynbHtmlVideoDTO;
import com.rss.nest.function.zsxcool.models.ZsxcoolHtmlCardDTO;
import com.rss.nest.models.webhtml.WebDataArticleDTO;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
import com.rss.nest.utils.date.DateParseUtil;

import java.util.Date;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/10/11 下午3:24:15
 * @description:
 */
public class ZsxcoolDataConvertToRssUtil {

    /**
     * 处理网页数据,转为可封装RSS的网页数据
     *
     * @param title               RSS标题
     * @param link                RSS链接
     * @param zsxcoolHtmlCardList 待处理资源数据集合
     * @return: com.rss.nest.models.webhtml.WebHtmlDataDTO
     * @author Lemon695
     * @date 2024/9/30 下午6:21
     */
    public static WebHtmlDataDTO checkParseItemListToRss(String title, String link, List<ZsxcoolHtmlCardDTO> zsxcoolHtmlCardList) {
        WebHtmlDataDTO webHtmlDataDTO = new WebHtmlDataDTO();
        List<WebDataArticleDTO> articleList = webHtmlDataDTO.getArticleList();

        if (zsxcoolHtmlCardList.isEmpty()) {
            return webHtmlDataDTO;
        }

        for (ZsxcoolHtmlCardDTO item : zsxcoolHtmlCardList) {
            String itemTitle = item.getTitle();
            String itemUrl = item.getHref();
            String itemDateStr = item.getDate();
            String imageUrl = item.getDataBg();

            //时间,2024/6/29
            Date createAtDate = DateParseUtil.parseDateStr(DateParseUtil.SDF_V2_YYYY_MM_DD, itemDateStr);
            if (createAtDate == null) {
                createAtDate = new Date();
            }

            StringBuilder itemContentBuilder = new StringBuilder();
            itemContentBuilder.append(itemTitle).append("<br>");
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

        webHtmlDataDTO.setTitle(title);
        webHtmlDataDTO.setDescription("");
        webHtmlDataDTO.setLink(link);
        webHtmlDataDTO.setArticleList(articleList);
        return webHtmlDataDTO;
    }
}
