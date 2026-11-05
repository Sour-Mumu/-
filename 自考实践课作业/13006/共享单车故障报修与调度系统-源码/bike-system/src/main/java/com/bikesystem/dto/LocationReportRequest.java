package com.bikesystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 定位上报请求 DTO。
 * 单车每 30s 上报一次，接口需具备高并发处理能力（目标峰值 2 万 QPS）。
 */
@Data
public class LocationReportRequest {

    @NotBlank(message = "bikeId 不能为空")
    private String bikeId;

    @NotNull(message = "经度不能为空")
    private Double lng;

    @NotNull(message = "纬度不能为空")
    private Double lat;

    /** 设备时间戳（毫秒） */
    private Long ts;
}
