package com.bikesystem.service;

import java.util.List;
import java.util.Map;

/**
 * TSP（旅行商问题）最优路线服务接口。
 * 生产环境调用 pgRouting 的 pgr_TSP / pgr_euclidianTSP 求解；
 * 本实现提供基于最近邻 + 2-opt 的近似算法，无需数据库依赖，便于单元测试。
 */
public interface TspService {

    /**
     * 求解访问一组故障点的最优顺序。
     *
     * @param points 故障点坐标，每个元素为 {"id": long, "lng": double, "lat": double}
     * @return 按访问顺序排列的故障点 id 列表
     */
    List<Long> solveTsp(List<Map<String, Object>> points);
}
