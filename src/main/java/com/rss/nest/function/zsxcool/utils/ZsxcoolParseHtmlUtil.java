package com.rss.nest.function.zsxcool.utils;

import com.rss.nest.function.rrdynb.models.RrdynbHtmlVideoDTO;
import com.rss.nest.function.zsxcool.models.ZsxcoolHtmlCardDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lemon695
 * @date: 2024/10/11 下午2:51:14
 * @description:
 */
public class ZsxcoolParseHtmlUtil {

    /**
     * 解析网页数据,获得网页主要数据
     *
     * @param document 网页内容
     * @return: java.util.List<com.rss.nest.function.zsxcool.models.ZsxcoolHtmlCardDTO>
     * @author Lemon695
     * @date 2024/10/11 下午3:03
     */
    public static List<ZsxcoolHtmlCardDTO> parseHtmlToCardList(Document document) {
        List<ZsxcoolHtmlCardDTO> cardList = new ArrayList<>(128);

        Elements videoElements = document.select(".post-item");
        for (Element postItem : videoElements) {
            String href = postItem.select("a").attr("href");
            String dataBg = postItem.select("a").attr("data-bg");
            String date = Objects.requireNonNull(postItem.select(".date").first()).text();
            // 取第一个title元素的文本
            String title = Objects.requireNonNull(postItem.select(".title").first()).text();

            ZsxcoolHtmlCardDTO entity = new ZsxcoolHtmlCardDTO();
            entity.setHref(href);
            entity.setDataBg(dataBg);
            entity.setDate(date);
            entity.setTitle(title);

            cardList.add(entity);
        }

        return cardList;
    }
}
