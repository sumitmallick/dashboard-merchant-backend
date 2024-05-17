package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class CacheBO {
    private final KmsBO kmsBO;
    private final RedisConfig redisConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheBO.class);

    @Autowired
    public CacheBO(KmsBO kmsBO, RedisConfig redisConfig) {
        this.kmsBO = kmsBO;
        this.redisConfig = redisConfig;
    }

    public void putInCache(String key, String value) {
        putInCache(key, value, true);
    }

    public void putInCache(String key, String value, boolean encrypt) {
        if (!StringUtils.isEmpty(value)) {
            Objects.requireNonNull(redisConfig.cacheManagerForAnyExpiry().getCache("cache"))
                    .put(key, encrypt ? kmsBO.encrypt(value) : value);
        }
    }

    public void putInCacheTTL5Sec(String key, String value) {
        if (!StringUtils.isEmpty(value)) {
            Objects.requireNonNull(redisConfig.cacheManager5Sec()
                    .getCache("cache")).put(key, kmsBO.encrypt(value));
        }
    }

    public String getDecryptedValueFromCache(String key) {
        String getFromCache = getFromCache(key);
        return StringUtils.isEmpty(getFromCache) ? null : kmsBO.decrypt(getFromCache);
    }

    public String getFromCache(String key) {
        Cache cache = redisConfig.cacheManagerForAnyExpiry().getCache("cache");
        if (!StringUtils.isEmpty(cache) && !StringUtils.isEmpty(cache.get(key)) &&
                !StringUtils.isEmpty(cache.get(key).get())) {
            return String.valueOf(cache.get(key).get());
        }
        return null;
    }

    public String getFromCacheTTL5Sec(String key) {
        Cache cache = redisConfig.cacheManager5Sec().getCache("cache");
        if (!StringUtils.isEmpty(cache) && !StringUtils.isEmpty(cache.get(key)) &&
                !StringUtils.isEmpty(cache.get(key).get())) {
            return String.valueOf(cache.get(key).get());
        }
        return null;
    }

    public boolean removeFromCache(String key) {
        boolean result = false;
        try {
            Cache cache = redisConfig.cacheManagerForAnyExpiry().getCache("cache");
            if (null != cache) {
                cache.evict(key);
                result = true;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred : {} while removing key from cache", e.getMessage());
        }
        return result;
    }

    public boolean removeFromCacheTTL5Sec(String key) {
        boolean result = false;
        try {
            Cache cache = redisConfig.cacheManager5Sec().getCache("cache");
            if (null != cache) {
                cache.evict(key);
                result = true;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred : {} while removing key from cache", e.getMessage());
        }
        return result;
    }
}
