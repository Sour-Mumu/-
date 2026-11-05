package com.bikesystem.controller;

import com.bikesystem.dto.ApiResponse;
import com.bikesystem.dto.LocationReportDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/location")
public class LocationController {

    private final RedisTemplate<String, String> redis;
    private final RabbitTemplate rabbit;
    private static final String KEY_PREFIX = "bike:loc:";

    public LocationController(RedisTemplate<String, String> redis, RabbitTemplate rabbit) {
        this.redis = redis;
        this.rabbit = rabbit;
    }

    /** 车辆定位上报：Redis 缓存最新坐标（30s 刷新），MQ 异步落库削峰 */
    @PostMapping
    public ApiResponse<Void> report(@Valid @RequestBody LocationReportDTO dto) {
        String key = KEY_PREFIX + dto.getBikeId();
        String val = dto.getLng() + "," + dto.getLat();
        redis.opsForValue().set(key, val);
        rabbit.convertAndSend("loc.exchange", "loc.key", dto);
        return ApiResponse.success(null);
    }
}
