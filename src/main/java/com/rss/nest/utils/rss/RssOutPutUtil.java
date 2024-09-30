package com.rss.nest.utils.rss;

import com.rometools.rome.feed.rss.*;
import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.rss.RssItem;
import com.rss.nest.utils.common.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Lemon695
 * @description: RSS输出格式
 * @date: 2024/8/30 下午4:19
 */
@Slf4j
public class RssOutPutUtil {

    /**
     * Rss输出格式化
     *
     * @param rssChannel RSS数据
     * @return RSS数据
     */
    public static Channel rssOutPut(RssChannel rssChannel) {
        if (rssChannel == null) {
            return null;
        }

        // 清理RSS Channel内容中的非法字符
        rssChannel = RssChannelDataUtil.cleanRssChannel(rssChannel);

        Channel channel = new Channel();
        channel.setFeedType("rss_2.0");
        channel.setTitle(rssChannel.getTitle());
        channel.setDescription(rssChannel.getDescription());
        channel.setLink(rssChannel.getLink());
        channel.setUri(rssChannel.getUri());
        channel.setGenerator("RSSNest");

        if (rssChannel.getImage() != null) {
            Image image = new Image();
            image.setUrl(rssChannel.getImage().getUrl());
            image.setTitle(rssChannel.getImage().getTitle());
            image.setHeight(32);
            image.setWidth(32);
            channel.setImage(image);
        }

        Date postDate = new Date();
        channel.setPubDate(postDate);

        List<Item> items = new ArrayList<>(100);
        channel.setItems(items);

        List<RssItem> rssItemList = rssChannel.getItems();

        for (RssItem rssItem : rssItemList) {
            Item item = new Item();
            item.setAuthor(rssItem.getAuthor());
            item.setLink(rssItem.getLink());

            String itemTitle = StringUtil.getEncodeText(rssItem.getTitle());
            item.setTitle(itemTitle);
            item.setUri(rssItem.getUri());
            item.setComments(rssItem.getComments());

            Category category = new Category();
            category.setValue("CORS");
            item.setCategories(Collections.singletonList(category));

            Description desc = new Description();
            desc.setValue(rssItem.getDescription());
            item.setDescription(desc);
            item.setPubDate(rssItem.getPubDate());

            items.add(item);
        }

        return channel;
    }

}
