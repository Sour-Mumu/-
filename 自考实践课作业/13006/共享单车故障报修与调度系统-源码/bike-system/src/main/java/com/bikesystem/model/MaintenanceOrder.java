package com.bikesystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaintenanceOrder {
    private Long orderId;
    private Long reportId;
    private Long repairmanId;
    private String status;
    private LocalDateTime arriveTime;
    private LocalDateTime finishTime;

    public void accept(Long repairmanId) {
        this.repairmanId = repairmanId;
        this.status = "已接单";
    }

    public void checkIn() { this.arriveTime = LocalDateTime.now(); this.status = "已到场"; }

    public void finish() { this.finishTime = LocalDateTime.now(); this.status = "已完成"; }
}
