package com.bikesystem.service.impl;

import com.bikesystem.config.RedisConfig;
import com.bikesystem.dto.LocationReportRequest;
import com.bikesystem.service.LocationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisLocationService implements LocationService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${bike.location.cache-key-prefix:bike:loc:}")
    private String keyPrefix;

    @Value("${bike.location.report-interval-seconds:30}")
    private int expireSeconds;

    public RedisLocationService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean reportLocation(LocationReportRequest request) {
        String key = keyPrefix + request.getBikeId();
        Map<String, Object> payload = new HashMap<>();
        payload.put("bikeId", request.getBikeId());
        payload.put("lng", request.getLng());
        payload.put("lat", request.getLat());
        payload.put("ts", request.getTs() != null ? request.getTs() : System.currentTimeMillis());
        try {
            redisTemplate.opsForValue().set(key, payload, expireSeconds * 2L, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            log.error("定位缓存写入失败: {}", request.getBikeId(), e);
            return false;
        }
    }
}
