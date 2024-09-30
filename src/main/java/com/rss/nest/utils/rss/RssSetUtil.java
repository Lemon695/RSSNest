package com.rss.nest.utils.rss;

import com.rss.nest.models.rss.RssChannel;
import com.rss.nest.models.rss.RssItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.text.MessageFormat;
import java.util.Date;

/**
 * @author Lemon695
 * @Description: RSS返回设置信息数据（Img、Video）
 * @Date: 2023/5/21 1:09 AM
 * @Modified By:
 */
@Slf4j
public class RssSetUtil {

    private static final String ACTION_0 = "{0}<br><br>";
    private static final String ACTION_1 = "{0}{1}<br><br>";
    
    /**
     * 设置RSS-Item描述
     *
     * @param title
     * @param time
     * @param description
     * @param imgInfo
     * @param videoInfo
     * @return
     */
    public static String setItemDescription(String title, String time, String description, String imgInfo, String videoInfo) {
        String result = "";
        if (StringUtils.isNotBlank(title)) {
            //{0}<br><br>
            result = MessageFormat.format(ACTION_0, title);
        }
        if (StringUtils.isNotBlank(time)) {
            //{0}{1}<br><br>
            result = MessageFormat.format(ACTION_1, result, time);
        }
        if (StringUtils.isNotBlank(description)) {
            result = MessageFormat.format(ACTION_1, result, description);
        }
        if (StringUtils.isNotBlank(imgInfo)) {
            result = MessageFormat.format(ACTION_1, result, imgInfo);
        }
        if (StringUtils.isNotBlank(videoInfo)) {
            result = MessageFormat.format(ACTION_1, result, videoInfo);
        }
        return result;
    }

    /**
     * @param itemTitle
     * @param itemUrl
     * @param itemDate
     * @param itemDescription
     * @param rssChannel
     */
    @NotNull
    @Contract("_, _, _, _, _ -> param5")
    public static RssChannel setRssChannelData(String itemTitle, String itemUrl, Date itemDate, String itemDescription, RssChannel rssChannel) {
        RssItem rssItem = new RssItem();
        rssItem.setTitle(itemTitle);
        rssItem.setAuthor("");
        rssItem.setLink(itemUrl);
        rssItem.setUri(itemUrl);
        rssItem.setPubDate(itemDate);
        rssItem.setDescription(itemDescription);
        rssChannel.getItems().add(rssItem);

        return rssChannel;
    }
}
