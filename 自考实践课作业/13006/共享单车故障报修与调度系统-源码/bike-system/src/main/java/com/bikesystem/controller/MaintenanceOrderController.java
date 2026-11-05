package com.bikesystem.controller;

import com.bikesystem.dto.ApiResponse;
import com.bikesystem.model.MaintenanceOrder;
import com.bikesystem.repository.MaintenanceOrderRepository;
import com.bikesystem.service.MaintenanceOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class MaintenanceOrderController {

    private final MaintenanceOrderService orderService;
    private final MaintenanceOrderRepository orderRepository;

    public MaintenanceOrderController(MaintenanceOrderService orderService,
                                     MaintenanceOrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public ApiResponse<List<MaintenanceOrder>> list(
            @RequestParam(required = false) MaintenanceOrder.OrderStatus status) {
        return ApiResponse.success(status == null
                ? orderRepository.findAll()
                : orderService.listByStatus(status));
    }

    /** 维修员接单 */
    @PutMapping("/{id}/accept")
    public ApiResponse<MaintenanceOrder> accept(@PathVariable Long id,
                                               @RequestParam Long repairmanId) {
        return ApiResponse.success(orderService.acceptOrder(id, repairmanId));
    }

    /** 到场打卡 */
    @PutMapping("/{id}/checkin")
    public ApiResponse<MaintenanceOrder> checkIn(@PathVariable Long id) {
        return ApiResponse.success(orderService.checkIn(id));
    }

    /** 拍照销单 */
    @PutMapping("/{id}/finish")
    public ApiResponse<MaintenanceOrder> finish(@PathVariable Long id,
                                               @RequestParam String record,
                                               @RequestParam String photo) {
        return ApiResponse.success(orderService.finishOrder(id, record, photo));
    }
}
