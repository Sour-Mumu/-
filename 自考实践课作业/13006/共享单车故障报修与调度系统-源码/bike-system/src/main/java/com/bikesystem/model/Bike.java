package com.bikesystem.model;

import com.bikesystem.statemachine.BikeStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Bike {
    private String bikeId;
    private BikeStatus status;
    private String lastLocation;
    private LocalDateTime putTime;

    public void transitTo(BikeStatus target) {
        this.status = target;
    }
}
