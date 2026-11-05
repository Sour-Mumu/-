package com.bikesystem.service;

import com.bikesystem.exception.TransitionException;
import com.bikesystem.model.Bike;
import com.bikesystem.statemachine.BikeStatus;
import com.bikesystem.statemachine.BikeStateMachine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BikeStateMachineTest {

    private BikeStateMachine machine;
    private Bike bike;

    @BeforeEach
    void setUp() {
        machine = new BikeStateMachine();
        bike = new Bike();
        bike.setBikeId("B001");
    }

    @Test
    void 初始入库后可投放() {
        bike.setStatus(BikeStatus.IN_STOCK);
        Bike target = new Bike();
        target.setStatus(BikeStatus.IN_USE);
        machine.transit(bike, target);
        assertEquals(BikeStatus.IN_USE, bike.getStatus());
    }

    @Test
    void 投放中可转入故障待修() {
        bike.setStatus(BikeStatus.IN_USE);
        Bike target = new Bike();
        target.setStatus(BikeStatus.FAULT_PENDING);
        machine.transit(bike, target);
        assertEquals(BikeStatus.FAULT_PENDING, bike.getStatus());
    }

    @Test
    void 投放中不可直接转报废_应抛异常() {
        bike.setStatus(BikeStatus.IN_USE);
        Bike target = new Bike();
        target.setStatus(BikeStatus.SCRAPPED);
        assertThrows(TransitionException.class, () -> machine.transit(bike, target));
    }

    @Test
    void 报废状态不可再流转() {
        bike.setStatus(BikeStatus.SCRAPPED);
        Bike target = new Bike();
        target.setStatus(BikeStatus.IN_USE);
        assertThrows(TransitionException.class, () -> machine.transit(bike, target));
    }
}
