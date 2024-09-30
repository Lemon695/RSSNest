package com.rss.nest.function.rrdynb.utils;

import com.rss.nest.function.rrdynb.models.RrdynbHtmlVideoDTO;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:02:16
 * @description:
 */
public class RrdynbParseHtmlUtil {

    /**
     * 解析网页数据,获得视频主要数据
     *
     * @param document 网页内容
     * @return: 视频数据集合
     * @author Lemon695
     * @date 2024/9/30 下午4:19
     */
    public static List<RrdynbHtmlVideoDTO> parseHtmlToVideoList(Document document) {
        List<RrdynbHtmlVideoDTO> videoList = new ArrayList<>();

        Elements videoElements = document.select("li.pure-g.shadow");

        for (Element videoElement : videoElements) {
            String title = videoElement.select("h2 a").text();
            String url = videoElement.select("h2 a").attr("href");
            String director = videoElement.select(".brief").text().split("导演: ")[1].split(" / ")[0];
            String brief = videoElement.select(".brief").text();
            // 提取日期
            String date = videoElement.select(".tags").text().split(" ")[0].trim();

            // 提取图片链接
            String imageUrl = videoElement.select(".pure-u-5-24 img").attr("data-original");

            RrdynbHtmlVideoDTO movie = new RrdynbHtmlVideoDTO(title, url, director, brief, date, imageUrl);
            videoList.add(movie);
        }

        return videoList;
    }
}
