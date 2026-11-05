package com.bikesystem.service.impl;

import com.bikesystem.model.Bike;
import com.bikesystem.model.BikeStatus;
import com.bikesystem.model.DispatchRoute;
import com.bikesystem.model.FaultReport;
import com.bikesystem.repository.BikeRepository;
import com.bikesystem.repository.DispatchRouteRepository;
import com.bikesystem.repository.FaultReportRepository;
import com.bikesystem.service.DispatchService;
import com.bikesystem.service.TspService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DispatchServiceImpl implements DispatchService {

    private final FaultReportRepository faultReportRepository;
    private final DispatchRouteRepository routeRepository;
    private final BikeRepository bikeRepository;
    private final TspService tspService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DispatchServiceImpl(FaultReportRepository faultReportRepository,
                              DispatchRouteRepository routeRepository,
                              BikeRepository bikeRepository,
                              TspService tspService) {
        this.faultReportRepository = faultReportRepository;
        this.routeRepository = routeRepository;
        this.bikeRepository = bikeRepository;
        this.tspService = tspService;
    }

    @Override
    public List<FaultReport> listFaultPoints() {
        return faultReportRepository.findByDispatched(false);
    }

    @Override
    @Transactional
    public DispatchRoute generateRoute() {
        List<Map<String, Object>> points = faultReportRepository.findByDispatched(false).stream()
                .map(r -> Map.<String, Object>of(
                        "id", r.getReportId(),
                        "lng", r.getLng() != null ? r.getLng() : 0.0,
                        "lat", r.getLat() != null ? r.getLat() : 0.0))
                .collect(Collectors.toList());

        List<Long> path = tspService.solveTsp(points);
        if (path.isEmpty()) {
            return DispatchRoute.builder()
                    .tspPath("[]")
                    .totalCost(BigDecimal.ZERO)
                    .totalDistance(0.0)
                    .createTime(LocalDateTime.now())
                    .build();
        }

        // 路径序列持久化为 JSON
        String json;
        try {
            json = objectMapper.writeValueAsString(path);
        } catch (Exception e) {
            throw new RuntimeException("路径序列化失败", e);
        }

        // 总里程估算（按顺序累加球面距离，单位米）
        double distance = 0;
        Map<Long, Map<String, Object>> map = points.stream()
                .collect(Collectors.toMap(p -> (Long) p.get("id"), p -> p));
        for (int i = 0; i < path.size() - 1; i++) {
            Map<String, Object> a = map.get(path.get(i));
            Map<String, Object> b = map.get(path.get(i + 1));
            distance += haversine(a, b);
        }

        // 标记已派单
        faultReportRepository.findByDispatched(false).forEach(r -> r.setDispatched(true));

        BigDecimal cost = calcDispatchCost(distance, 2.5)
                .setScale(2, RoundingMode.HALF_UP);

        return routeRepository.save(DispatchRoute.builder()
                .tspPath(json)
                .totalCost(cost)
                .totalDistance(distance)
                .createTime(LocalDateTime.now())
                .build());
    }

    @Override
    public double calcMonthlyFaultRate(int year, int month) {
        long totalInUse = bikeRepository.countByStatus(BikeStatus.IN_USE);
        if (totalInUse == 0) return 0.0;
        long faultCount = bikeRepository.countByStatus(BikeStatus.FAULT_PENDING)
                + bikeRepository.countByStatus(BikeStatus.REPAIRING);
        return BigDecimal.valueOf(faultCount * 100.0 / totalInUse)
                .setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public BigDecimal calcDispatchCost(double meters, double unitPricePerKm) {
        double km = meters / 1000.0;
        return BigDecimal.valueOf(km * unitPricePerKm);
    }

    private double haversine(Map<String, Object> a, Map<String, Object> b) {
        final int R = 6371000;
        double lat1 = Math.toRadians(((Number) a.get("lat")).doubleValue());
        double lat2 = Math.toRadians(((Number) b.get("lat")).doubleValue());
        double dLat = Math.toRadians(((Number) b.get("lat")).doubleValue()
                - ((Number) a.get("lat")).doubleValue());
        double dLng = Math.toRadians(((Number) b.get("lng")).doubleValue()
                - ((Number) a.get("lng")).doubleValue());
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * R * Math.asin(Math.min(1, Math.sqrt(h)));
    }
}
