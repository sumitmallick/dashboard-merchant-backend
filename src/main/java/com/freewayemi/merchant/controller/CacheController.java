package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.dto.CacheRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class CacheController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheController.class);
    private final CacheManager cacheManager;

    @Autowired
    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @PostMapping("/internal/api/v1/cache/delete")
    public void deleteCache(@RequestBody CacheRequest cacheRequest) {
        LOGGER.info("Request received to delete cache with params: {}", cacheRequest);
        Cache cache = cacheManager.getCache(cacheRequest.getCacheName());
        if (Objects.nonNull(cache)) {
            LOGGER.info("Clearing cache with name: {}", cacheRequest.getCacheName());
            cache.clear();
            return;
        }
        LOGGER.info("Cache not found with name: {}", cacheRequest.getCacheName());
    }
}
