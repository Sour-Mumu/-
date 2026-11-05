package com.bikesystem.service;

import com.bikesystem.model.MaintenanceOrder;
import com.bikesystem.model.MaintenanceOrder.OrderStatus;

import java.util.List;

public interface MaintenanceOrderService {

    /** 创建工单（由报修单触发，状态 PENDING） */
    MaintenanceOrder createOrder(Long reportId, String bikeId);

    /** 维修员接单 */
    MaintenanceOrder acceptOrder(Long orderId, Long repairmanId);

    /** 到场打卡 */
    MaintenanceOrder checkIn(Long orderId);

    /** 提交维修记录并拍照销单 */
    MaintenanceOrder finishOrder(Long orderId, String repairRecord, String photoUrl);

    /** 取消工单 */
    MaintenanceOrder cancelOrder(Long orderId);

    /** 按状态查询 */
    List<MaintenanceOrder> listByStatus(OrderStatus status);

    /** 统计维修员绩效：已完成数量、平均耗时（分钟） */
    List<Object[]> statRepairmanPerformance();
}
