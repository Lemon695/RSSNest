package com.rss.nest.utils.rss;

import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.rss.RssItem;
import com.rss.nest.utils.xml.XmlCharCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lemon695
 * @Description:
 * @Date: 2023/5/21 12:55 AM
 * @Modified By:
 */
@Slf4j
public class RssChannelDataUtil {

    /**
     * 设置基础数据
     * setRssChannel
     *
     * @param title
     * @param url
     * @param description
     * @return
     */
    public static RssChannel setRssChannel(String title, String url, String description) {
        return new RssChannel(title, url, url, description, new ArrayList<>(150));
    }

    /**
     * 清理非法字符串
     *
     * @param rssChannel RSS响应数据
     * @return: Rss响应数据
     * @author Lemon695
     * @date 2024/9/14 下午3:40
     */
    public static RssChannel cleanRssChannel(RssChannel rssChannel) {
        // 假设RssChannel有一个getDescription方法可以获取描述
        // 和一个setDescription方法可以设置描述
        if (rssChannel != null) {
            String description = rssChannel.getDescription();
            if (description != null) {
                description = XmlCharCleaner.sanitizeXmlString(description);
                rssChannel.setDescription(description);
            }

            List<RssItem> items = rssChannel.getItems();
            if (items != null && !items.isEmpty()) {
                for (RssItem rssItem : items) {
                    String itemDescription = rssItem.getDescription();
                    if (StringUtils.isNotBlank(itemDescription)) {
                        itemDescription = XmlCharCleaner.sanitizeXmlString(itemDescription);
                        rssItem.setDescription(itemDescription);
                    }
                }
            }
        }

        return rssChannel;
    }

}
