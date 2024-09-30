package com.rss.nest.function.rrdynb.core;

import com.rss.nest.models.rss.RssChannel;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午4:26:16
 * @description: 人人影视-接口逻辑
 */
public interface IRrdynbRssApiCoreLogicService {

    /**
     * (人人电影网-最新视频列表)
     *
     * @param category 分类
     * @return: com.rss.nest.models.rss.RssChannel
     * @author Lemon695
     * @date 2024/9/30 下午6:10
     */
    RssChannel dianPingUserFeeds(String category);
}
