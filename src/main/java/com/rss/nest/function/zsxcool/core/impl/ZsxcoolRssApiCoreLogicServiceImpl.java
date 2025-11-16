package com.rss.nest.function.zsxcool.core.impl;

import com.rss.nest.function.rrdynb.enums.RrdynbCategoryEnum;
import com.rss.nest.function.rrdynb.models.RrdynbHtmlVideoDTO;
import com.rss.nest.function.rrdynb.utils.RrdynbDataConvertToRssUtil;
import com.rss.nest.function.rrdynb.utils.RrdynbParseHtmlUtil;
import com.rss.nest.function.zsxcool.core.IZsxcoolRssApiCoreLogicService;
import com.rss.nest.function.zsxcool.enums.ZsxcoolCategoryEnum;
import com.rss.nest.function.zsxcool.models.ZsxcoolHtmlCardDTO;
import com.rss.nest.function.zsxcool.utils.ZsxcoolDataConvertToRssUtil;
import com.rss.nest.function.zsxcool.utils.ZsxcoolParseHtmlUtil;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.webhtml.WebHtmlDataDTO;
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
 * @date: 2024/10/11 下午3:07:15
 * @description:
 */
@Slf4j
@Service
public class ZsxcoolRssApiCoreLogicServiceImpl implements IZsxcoolRssApiCoreLogicService {

    @Resource
    private OkHttpClientUtil okHttpClientUtil;

    /**
     * 字节制造-最新资源列表
     *
     * @param category 分类
     * @return: RSS数据
     * @author Lemon695
     * @date 2024/10/11 下午3:41
     */
    @Override
    public RssChannel categoryResourceFeed(String category) {
        ZsxcoolCategoryEnum zsxcoolCategoryEnum = ZsxcoolCategoryEnum.getEnumDataByDataType(category);
        if (zsxcoolCategoryEnum == null) {
            return null;
        }

        String mainTitle = zsxcoolCategoryEnum.getName() + " - 字节智造";
        String link = zsxcoolCategoryEnum.getUrl();

        Headers headers = new Headers.Builder().build();
        String result = okHttpClientUtil.doGet(link, headers);
        Document document = Jsoup.parse(result);

        List<ZsxcoolHtmlCardDTO> zsxcoolHtmlCardList = ZsxcoolParseHtmlUtil.parseHtmlToCardList(document);

        WebHtmlDataDTO webHtmlDataDTO = ZsxcoolDataConvertToRssUtil.checkParseItemListToRss(mainTitle, link, zsxcoolHtmlCardList);

        //解析数据转为RSS
        return HtmlDataConvertToRssUtil.analysisDocDataToRss(webHtmlDataDTO);
    }
}
