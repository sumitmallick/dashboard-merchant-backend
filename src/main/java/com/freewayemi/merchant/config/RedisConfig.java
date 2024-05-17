package com.freewayemi.merchant.config;

import com.netflix.discovery.provider.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Collections;

@Configuration
@EnableCaching
public class RedisConfig {

    private final String redisHostName;
    private final int redisPort;
    private final String redisPrefix;
    private final boolean sslEnabled;
    private final Integer ttl;
    private final boolean clusterMode;

    @Autowired
    public RedisConfig(@Value("${redis.hostname}") String redisHostName, @Value("${redis.port}") int redisPort,
                       @Value("${redis.prefix}") String redisPrefix, @Value("${redis.sslEnabled}") boolean sslEnabled,
                       @Value("${redis-key-ttl}") Integer ttl, @Value("${redis.cluster.mode}") boolean clusterMode) {
        this.redisHostName = redisHostName;
        this.redisPort = redisPort;
        this.redisPrefix = redisPrefix;
        this.sslEnabled = sslEnabled;
        this.ttl = ttl;
        this.clusterMode = clusterMode;
    }

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisClientConfiguration jedisConfig;
        jedisConfig = sslEnabled ? JedisClientConfiguration.builder().useSsl().build()
                : JedisClientConfiguration.builder().build();
        if (clusterMode) {
            RedisClusterConfiguration redisClusterConfiguration =
                    new RedisClusterConfiguration(Collections.singleton(redisHostName + ":" + redisPort));
            return new JedisConnectionFactory(redisClusterConfiguration, jedisConfig);
        }
        RedisStandaloneConfiguration redisStandaloneConfiguration =
                new RedisStandaloneConfiguration(redisHostName, redisPort);
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisConfig);
    }

    @Bean(value = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;

    }

    private RedisCacheConfiguration getRedisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new GenericJackson2JsonRedisSerializer()))
                .prefixKeysWith(redisPrefix);
    }

    @Bean(name = "cacheManager1Day")
    public CacheManager cacheManager1Day() {
        Duration expiration = Duration.ofDays(1);
        return RedisCacheManager.builder(jedisConnectionFactory())
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig().prefixKeysWith(redisPrefix).entryTtl(expiration))
                .build();
    }

    @Primary
    @Bean(name = "cacheManagerForAnyExpiry")
    public CacheManager cacheManagerForAnyExpiry() {
        Duration expiration = Duration.ofSeconds(ttl);
        return RedisCacheManager.builder(jedisConnectionFactory())
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig().prefixKeysWith(redisPrefix).entryTtl(expiration))
                .build();
    }

    @Bean(name = "cacheManagerWithTTL")
    public CacheManager cacheManager5Sec() {
        Duration expiration = Duration.ofSeconds(5);
        return RedisCacheManager.builder(jedisConnectionFactory())
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig().prefixKeysWith(redisPrefix).entryTtl(expiration))
                .build();
    }
    @Bean(name = "cacheManagerForProducts")
    public CacheManager cacheManagerForProducts(){
        Duration expiration = Duration.ofSeconds(ttl);
        return RedisCacheManager.builder(jedisConnectionFactory())
                .cacheDefaults(
                        RedisCacheConfiguration.defaultCacheConfig()
                                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                                .prefixKeysWith(redisPrefix).entryTtl(expiration)
                ).build();
    }

}
