package com.bikesystem.model;

/**
 * 车辆生命周期状态机枚举：
 * 入库(IN_STOCK) -> 投放(IN_USE) -> 故障待修(FAULT_PENDING) -> 维修中(REPAIRING) -> 投放(IN_USE)
 *                                                                              -> 报废(SCRAPPED)
 */
public enum BikeStatus {
    IN_STOCK("入库"),
    IN_USE("投放中"),
    FAULT_PENDING("故障待修"),
    REPAIRING("维修中"),
    SCRAPPED("已报废");

    private final String description;

    BikeStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
