package com.rss.nest.config;

import com.rss.nest.config.redis.CustomKeyPrefix;
import com.rss.nest.config.redis.FastJson2JsonRedisSerializer;
import com.rss.nest.framework.listener.RedisKeyExpirationListener;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

/**
 * @author Lemon695
 * @Description:
 * @Date: 2023/4/16 10:28 PM
 * @Modified By:
 */
@Configuration
@Component
public class RedisCustomConfig extends CachingConfigurerSupport {

    /**
     * 配置lettuce连接池
     *
     * @return
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.data.redis.lettuce.pool")
    public GenericObjectPoolConfig redisPool() {
        return new GenericObjectPoolConfig<>();
    }

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.database}")
    private String database;

    /**
     * 核心服务redis配置
     *
     * @return
     */
    @Bean("redisConfigCore")
    @ConfigurationProperties(prefix = "spring.data.redis")
    public RedisStandaloneConfiguration redisConfigCore() {
        return new RedisStandaloneConfiguration(host);
    }

    /**
     * 配置第一个数据源的连接工厂
     * 这里注意：需要添加@Primary 指定bean的名称，目的是为了创建两个不同名称的LettuceConnectionFactory
     *
     * @param config
     * @param redisConfig
     * @return
     */
    @Bean("redisFactory")
    @Primary
    public LettuceConnectionFactory factory(GenericObjectPoolConfig config, @Qualifier("redisConfigCore") RedisStandaloneConfiguration redisConfig) {
        LettuceClientConfiguration clientConfiguration = LettucePoolingClientConfiguration.builder().poolConfig(config).build();
        return new LettuceConnectionFactory(redisConfig, clientConfiguration);
    }

    @Bean("redisTemplate")
    @Primary
    public RedisTemplate<String, String> redisTemplate(@Qualifier("redisFactory") RedisConnectionFactory factory) {
        return getStringStringRedisTemplate(factory);
    }

    @Bean("objRedisTemplate")
    @Primary
    public RedisTemplate<Object, Object> objRedisTemplate(@Qualifier("redisFactory") RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        final FastJson2JsonRedisSerializer<Object> serializer = new FastJson2JsonRedisSerializer<Object>(Object.class);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 设置序列化方式
     *
     * @param factory
     * @return
     */
    private StringRedisTemplate getStringStringRedisTemplate(RedisConnectionFactory factory) {
        final StringRedisTemplate template = new StringRedisTemplate(factory);
        final StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 设置序列化方式
     *
     * @param factory
     * @return
     */
    private StringRedisTemplate getRedisTemplate(RedisConnectionFactory factory) {
        final StringRedisTemplate template = new StringRedisTemplate(factory);
        final StringRedisSerializer stringSerializer = new StringRedisSerializer();
        final FastJson2JsonRedisSerializer<Object> serializer = new FastJson2JsonRedisSerializer<Object>(Object.class);
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * SpringCache-配置
     *
     * @param cacheProperties
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        // 获取Properties中Redis的配置信息
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        // 获取RedisCacheConfiguration的默认配置对象
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 指定序列化器为GenericJackson2JsonRedisSerializer
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 过期时间设置
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        // 替换前缀生成器（有前缀和无前缀）
        config = config.computePrefixWith(CustomKeyPrefix.simple());
        if (redisProperties.getKeyPrefix() != null) {
            config = config.computePrefixWith(CustomKeyPrefix.prefixed(redisProperties.getKeyPrefix()));
        }
        // 缓存空值配置
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        // 是否启用前缀
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

    @Bean
    public DefaultRedisScript<Long> limitScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(limitScriptText());
        redisScript.setResultType(Long.class);
        return redisScript;
    }

    /**
     * 配置Redis监听器
     * <p>
     * 这里的__keyevent@0__:expired指的是订阅Redis数据库0的键过期事件。根据需要，你可以调整数据库编号。
     *
     * @param connectionFactory
     * @param listener
     * @return: org.springframework.data.redis.listener.RedisMessageListenerContainer
     * @author Lemon695
     * @date 2024/9/18 上午11:01
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       RedisKeyExpirationListener listener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        // 订阅 Redis 的过期事件
        String patternTopic = String.format("__keyevent@%s__:expired", database);
        container.addMessageListener(listener, new PatternTopic(patternTopic));

        return container;
    }

    /**
     * 限流脚本
     */
    private String limitScriptText() {
        return "local key = KEYS[1]\n" +
                "local count = tonumber(ARGV[1])\n" +
                "local time = tonumber(ARGV[2])\n" +
                "local current = redis.call('get', key);\n" +
                "if current and tonumber(current) > count then\n" +
                "    return tonumber(current);\n" +
                "end\n" +
                "current = redis.call('incr', key)\n" +
                "if tonumber(current) == 1 then\n" +
                "    redis.call('expire', key, time)\n" +
                "end\n" +
                "return tonumber(current);";
    }

}
