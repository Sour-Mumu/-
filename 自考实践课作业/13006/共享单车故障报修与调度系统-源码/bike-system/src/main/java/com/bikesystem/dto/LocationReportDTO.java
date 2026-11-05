package com.bikesystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LocationReportDTO {
    @NotBlank(message = "车辆ID不能为空")
    private String bikeId;

    @NotNull(message = "经度不能为空")
    private Double lng;

    @NotNull(message = "纬度不能为空")
    private Double lat;

    private Long timestamp;
}
