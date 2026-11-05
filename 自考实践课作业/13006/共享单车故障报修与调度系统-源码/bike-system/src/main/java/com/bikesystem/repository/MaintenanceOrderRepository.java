package com.bikesystem.repository;

import com.bikesystem.model.MaintenanceOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MaintenanceOrderRepository extends JpaRepository<MaintenanceOrder, Long> {

    List<MaintenanceOrder> findByRepairmanId(Long repairmanId);

    List<MaintenanceOrder> findByStatus(MaintenanceOrder.OrderStatus status);

    List<MaintenanceOrder> findByFinishTimeBetween(LocalDateTime start, LocalDateTime end);
}
