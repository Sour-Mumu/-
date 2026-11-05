package com.bikesystem.model;

import lombok.Data;
import java.util.List;

@Data
public class DispatchRoute {
    private Long routeId;
    private List<Long> tspPath;
    private Double totalCost;
}
