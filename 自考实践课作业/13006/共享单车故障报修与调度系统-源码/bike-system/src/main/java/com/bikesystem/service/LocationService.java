package com.bikesystem.service;

import com.bikesystem.dto.LocationReportRequest;

/**
 * 单车定位上报服务。
 * 设计要点：
 * 1. Redis 缓存最新坐标（30s 刷新一次），降低数据库写压力；
 * 2. 批量异步落库，削峰应对 5 万辆车 × 30s 上报（约 1666 QPS 常态，
 *    峰值按 2 万 QPS 设计，需引入消息队列 + 分库分表）；
 * 3. 同一 bikeId 并发上报以最后写入为准。
 */
public interface LocationService {

    /** @return 缓存是否写入成功 */
    boolean reportLocation(LocationReportRequest request);
}
