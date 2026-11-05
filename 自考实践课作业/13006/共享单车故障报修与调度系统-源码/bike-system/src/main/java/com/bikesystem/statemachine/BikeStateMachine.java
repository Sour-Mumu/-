package com.bikesystem.statemachine;

import java.util.Set;

public class BikeStateMachine {

    /** 合法迁移表：key=当前状态，value=允许转入的目标状态集合 */
    private static final java.util.Map<BikeStatus, Set<BikeStatus>> TRANSITIONS = new java.util.HashMap<>();

    static {
        TRANSITIONS.put(BikeStatus.IN_STOCK, Set.of(BikeStatus.IN_USE));
        TRANSITIONS.put(BikeStatus.IN_USE, Set.of(BikeStatus.FAULT_PENDING, BikeStatus.IN_STOCK));
        TRANSITIONS.put(BikeStatus.FAULT_PENDING, Set.of(BikeStatus.REPAIRING));
        TRANSITIONS.put(BikeStatus.REPAIRING, Set.of(BikeStatus.IN_USE, BikeStatus.SCRAPPED));
        TRANSITIONS.put(BikeStatus.SCRAPPED, Set.of());
    }

    public boolean isAllowed(BikeStatus from, BikeStatus to) {
        Set<BikeStatus> allowed = TRANSITIONS.getOrDefault(from, Set.of());
        return allowed.contains(to);
    }

    public void transit(Bike from, Bike toStatus) {
        if (!isAllowed(from.getStatus(), toStatus.getStatus())) {
            throw new TransitionException(from.getStatus(), toStatus.getStatus());
        }
        from.setStatus(toStatus.getStatus());
    }
}
