package com.bikesystem.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FaultReport {
    private Long reportId;
    private String bikeId;
    private String faultType;
    private String photoUrl;
    private Double lng;
    private Double lat;
    private LocalDateTime createTime;
    private Long faultCount;
}
