package com.bikesystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportFaultRequest {

    @NotBlank(message = "车辆编号不能为空")
    private String bikeId;

    @NotBlank(message = "故障类型不能为空")
    private String faultType;

    private String photoUrl;

    private Double lng;

    private Double lat;

    private Long reporterId;
}
