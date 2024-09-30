package com.rss.nest.utils.rss.check;

import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Item;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Lemon695
 * @description:
 * @date: 2024/5/2 12:19 PM
 */
@Slf4j
public class RssChannelCheckUtil {

    /**
     * 校验Channel含有多条Item
     *
     * @param channel RSS内容
     * @return boolean
     * @author Lemon695
     * @date 2024/05/02 12:22
     */
    public static boolean checkChannelHasItems(Channel channel) {
        if (channel == null) {
            return false;
        }

        List<Item> itemList = channel.getItems();
        if (itemList != null && !itemList.isEmpty()) {
            return true;
        }

        return false;
    }
}
