package com.bikesystem.service.impl;

import com.bikesystem.service.TspService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * TSP 近似求解：最近邻启发式 + 2-opt 局部优化。
 * <p>
 * 生产环境可替换为 pgRouting 调用：{@code SELECT * FROM pgr_TSP(...)}。
 * 时间复杂度 O(n^2)，n<=50 时毫秒级返回，满足调度看板实时性要求。
 */
@Slf4j
@Service
public class NearestNeighborTspService implements TspService {

    @Value("${bike.tsp.max-nodes:50}")
    private int maxNodes;

    @Override
    public List<Long> solveTsp(List<Map<String, Object>> points) {
        if (points == null || points.isEmpty()) {
            return Collections.emptyList();
        }
        if (points.size() == 1) {
            return Collections.singletonList(toLong(points.get(0).get("id")));
        }

        List<Map<String, Object>> work = new ArrayList<>(points);
        if (work.size() > maxNodes) {
            log.warn("故障点数量 {} 超过上限 {}，截断处理", work.size(), maxNodes);
            work = work.subList(0, maxNodes);
        }

        // 最近邻构造初始回路
        List<Integer> tour = nearestNeighbor(work);
        // 2-opt 改进
        twoOpt(tour, work);

        List<Long> result = new ArrayList<>(tour.size());
        for (int idx : tour) {
            result.add(toLong(work.get(idx).get("id")));
        }
        log.debug("TSP 求解完成，点数={}，路径={}", work.size(), result);
        return result;
    }

    private List<Integer> nearestNeighbor(List<Map<String, Object>> points) {
        int n = points.size();
        boolean[] visited = new boolean[n];
        List<Integer> tour = new ArrayList<>(n);
        int current = 0;
        visited[current] = true;
        tour.add(current);
        for (int step = 1; step < n; step++) {
            int next = -1;
            double minDist = Double.MAX_VALUE;
            double[] c = coords(points.get(current));
            for (int j = 0; j < n; j++) {
                if (visited[j]) continue;
                double d = haversine(c, coords(points.get(j)));
                if (d < minDist) {
                    minDist = d;
                    next = j;
                }
            }
            visited[next] = true;
            current = next;
            tour.add(current);
        }
        return tour;
    }

    /**
     * 2-opt 局部优化：反转路径片段以降低总距离。
     */
    private void twoOpt(List<Integer> tour, List<Map<String, Object>> points) {
        int n = tour.size();
        boolean improved = true;
        int[][] dist = precomputeDist(points);
        while (improved) {
            improved = false;
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    if (gain(tour, dist, i, j) > 0) {
                        reverse(tour, i, j);
                        improved = true;
                    }
                }
            }
        }
    }

    private double gain(List<Integer> tour, int[][] dist, int i, int j) {
        int n = tour.size();
        int a = tour.get(i);
        int b = tour.get((i + 1) % n);
        int c = tour.get(j);
        int d = tour.get((j + 1) % n);
        return dist[a][b] + dist[c][d] - dist[a][c] - dist[b][d];
    }

    private void reverse(List<Integer> tour, int i, int j) {
        while (i < j) {
            Collections.swap(tour, i++, j--);
        }
    }

    private int[][] precomputeDist(List<Map<String, Object>> points) {
        int n = points.size();
        int[][] d = new int[n][n];
        for (int i = 0; i < n; i++) {
            double[] ci = coords(points.get(i));
            for (int j = i + 1; j < n; j++) {
                double dist = haversine(ci, coords(points.get(j)));
                d[i][j] = d[j][i] = (int) Math.round(dist);
            }
        }
        return d;
    }

    /** 半正矢公式，返回米 */
    private double haversine(double[] a, double[] b) {
        final int R = 6371000;
        double lat1 = Math.toRadians(a[1]);
        double lat2 = Math.toRadians(b[1]);
        double dLat = Math.toRadians(b[1] - a[1]);
        double dLng = Math.toRadians(b[0] - a[0]);
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * R * Math.asin(Math.min(1, Math.sqrt(h)));
    }

    private double[] coords(Map<String, Object> p) {
        return new double[]{
                ((Number) p.get("lng")).doubleValue(),
                ((Number) p.get("lat")).doubleValue()
        };
    }

    private Long toLong(Object id) {
        return ((Number) id).longValue();
    }
}
