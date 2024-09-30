package com.rss.nest.service;

/**
 * @author Lemon695
 * @date: 2024/9/30 下午11:07:23
 * @description:
 */
public interface IRedisService {

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    Object get(String key);

    /**
     * 设定小时-存储redis
     *
     * @param redisKey
     * @param redisValue
     * @param hours
     */
    void setValueByHours(String redisKey, String redisValue, int hours);

    /**
     * 设定天-存储redis
     *
     * @param redisKey   KEY值
     * @param redisValue 缓存数据
     * @param days       天
     */
    void setValueByDays(String redisKey, String redisValue, int days);

}
