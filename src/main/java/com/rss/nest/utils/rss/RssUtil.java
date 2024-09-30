package com.rss.nest.utils.rss;

import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;

/**
 * @author Lemon695
 * @date: 2024/10/1 上午12:16:00
 * @description:
 */
public class RssUtil {

    /**
     * Channel转为Xml格式
     *
     * @param channel RSS数据
     */
    public static String rssChannelOutPutXml(com.rometools.rome.feed.rss.Channel channel) {
        WireFeedOutput out = new WireFeedOutput();
        try {
            return out.outputString(channel);
        } catch (FeedException e) {
            e.printStackTrace();
        }

        return null;
    }
}
