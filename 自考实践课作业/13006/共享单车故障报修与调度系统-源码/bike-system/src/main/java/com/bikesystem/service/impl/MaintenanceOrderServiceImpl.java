package com.bikesystem.service.impl;

import com.bikesystem.exception.BusinessException;
import com.bikesystem.model.Bike;
import com.bikesystem.model.FaultReport;
import com.bikesystem.model.MaintenanceOrder;
import com.bikesystem.repository.BikeRepository;
import com.bikesystem.repository.FaultReportRepository;
import com.bikesystem.repository.MaintenanceOrderRepository;
import com.bikesystem.service.MaintenanceOrderService;
import com.bikesystem.statemachine.BikeStateMachine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MaintenanceOrderServiceImpl implements MaintenanceOrderService {

    private final MaintenanceOrderRepository orderRepository;
    private final FaultReportRepository faultReportRepository;
    private final BikeRepository bikeRepository;
    private final BikeStateMachine stateMachine;

    public MaintenanceOrderServiceImpl(MaintenanceOrderRepository orderRepository,
                                      FaultReportRepository faultReportRepository,
                                      BikeRepository bikeRepository,
                                      BikeStateMachine stateMachine) {
        this.orderRepository = orderRepository;
        this.faultReportRepository = faultReportRepository;
        this.bikeRepository = bikeRepository;
        this.stateMachine = stateMachine;
    }

    @Override
    @Transactional
    public MaintenanceOrder createOrder(Long reportId, String bikeId) {
        FaultReport report = faultReportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException("报修单不存在: " + reportId));
        MaintenanceOrder order = MaintenanceOrder.builder()
                .reportId(reportId)
                .bikeId(bikeId)
                .status(MaintenanceOrder.OrderStatus.PENDING)
                .build();
        // 报修触发车辆状态：投放 -> 故障待修
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new BusinessException("车辆不存在: " + bikeId));
        stateMachine.transit(bike, com.bikesystem.model.BikeStatus.FAULT_PENDING);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public MaintenanceOrder acceptOrder(Long orderId, Long repairmanId) {
        MaintenanceOrder order = requireOrder(orderId);
        assertStatus(order, MaintenanceOrder.OrderStatus.PENDING);
        order.setRepairmanId(repairmanId);
        order.setStatus(MaintenanceOrder.OrderStatus.ACCEPTED);
        order.setAcceptTime(LocalDateTime.now());
        Bike bike = bikeRepository.findById(order.getBikeId())
                .orElseThrow(() -> new BusinessException("车辆不存在"));
        stateMachine.transit(bike, com.bikesystem.model.BikeStatus.REPAIRING);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public MaintenanceOrder checkIn(Long orderId) {
        MaintenanceOrder order = requireOrder(orderId);
        assertStatus(order, MaintenanceOrder.OrderStatus.ACCEPTED);
        order.setStatus(MaintenanceOrder.OrderStatus.CHECKED_IN);
        order.setArriveTime(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public MaintenanceOrder finishOrder(Long orderId, String repairRecord, String photoUrl) {
        MaintenanceOrder order = requireOrder(orderId);
        if (order.getStatus() != MaintenanceOrder.OrderStatus.CHECKED_IN
                && order.getStatus() != MaintenanceOrder.OrderStatus.ACCEPTED) {
            throw new BusinessException("当前状态不允许销单: " + order.getStatus());
        }
        order.setStatus(MaintenanceOrder.OrderStatus.FINISHED);
        order.setRepairRecord(repairRecord);
        order.setFinishPhotoUrl(photoUrl);
        order.setFinishTime(LocalDateTime.now());
        Bike bike = bikeRepository.findById(order.getBikeId())
                .orElseThrow(() -> new BusinessException("车辆不存在"));
        stateMachine.transit(bike, com.bikesystem.model.BikeStatus.IN_USE);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public MaintenanceOrder cancelOrder(Long orderId) {
        MaintenanceOrder order = requireOrder(orderId);
        if (order.getStatus() == MaintenanceOrder.OrderStatus.FINISHED) {
            throw new BusinessException("已销单工单不可取消");
        }
        order.setStatus(MaintenanceOrder.OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public List<MaintenanceOrder> listByStatus(MaintenanceOrder.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public List<Object[]> statRepairmanPerformance() {
        // 简化：返回各维修员已完成数、平均耗时（分钟）
        List<MaintenanceOrder> finished = orderRepository.findAll().stream()
                .filter(o -> o.getStatus() == MaintenanceOrder.OrderStatus.FINISHED
                        && o.getAcceptTime() != null && o.getFinishTime() != null)
                .collect(Collectors.toList());
        return finished.stream()
                .collect(Collectors.groupingBy(MaintenanceOrder::getRepairmanId))
                .entrySet().stream()
                .map(e -> {
                    Long id = e.getKey();
                    List<MaintenanceOrder> list = e.getValue();
                    double avg = list.stream()
                            .mapToLong(o -> Duration.between(o.getAcceptTime(), o.getFinishTime()).toMinutes())
                            .average().orElse(0);
                    return new Object[]{id, list.size(), Math.round(avg)};
                })
                .collect(Collectors.toList());
    }

    private MaintenanceOrder requireOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("工单不存在: " + orderId));
    }

    private void assertStatus(MaintenanceOrder order, MaintenanceOrder.OrderStatus expected) {
        if (order.getStatus() != expected) {
            throw new BusinessException(
                    String.format("状态不符，期望 %s，实际 %s", expected, order.getStatus()));
        }
    }
}
