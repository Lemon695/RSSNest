package com.rss.nest.framework.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/**
 * @author Lemon695
 * @date: 2024/9/18 上午10:52:10
 * @description:
 */
@Slf4j
@Service
public class RedisKeyExpirationListener implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        log.info("Expired Key: {}", expiredKey);
        // 在这里统计过期的Key数量
    }
}
