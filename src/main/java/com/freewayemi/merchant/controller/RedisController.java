package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.CacheBO;
import com.freewayemi.merchant.utils.RedisKeysConstants;
import com.freewayemi.merchant.utils.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/redis")
public class RedisController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisController.class);

    private final CacheBO cacheBO;

    @Autowired
    public RedisController(CacheBO cacheBO) {
        this.cacheBO = cacheBO;
    }

    @PostMapping("/clearCache")
    public void clearCache(@RequestParam("key") String key) {
        cacheBO.removeFromCache(key);
    }

    @GetMapping("/getCacheByKey")
    public String getCacheByKey(@RequestParam("key") String key) {
        if (RedisKeyUtil.isEncrypted(key)) {
            return cacheBO.getDecryptedValueFromCache(key);
        }
        return cacheBO.getFromCache(key);
    }
}