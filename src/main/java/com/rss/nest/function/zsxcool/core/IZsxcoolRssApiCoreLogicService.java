package com.rss.nest.function.zsxcool.core;

import com.rss.nest.models.rss.RssChannel;

/**
 * @author Lemon695
 * @date: 2024/10/11 下午3:07:15
 * @description:
 */
public interface IZsxcoolRssApiCoreLogicService {

    /**
     * 字节制造-最新资源列表
     *
     * @param category 分类
     * @return: RSS数据
     * @author Lemon695
     * @date 2024/10/11 下午3:41
     */
    RssChannel categoryResourceFeed(String category);
}
