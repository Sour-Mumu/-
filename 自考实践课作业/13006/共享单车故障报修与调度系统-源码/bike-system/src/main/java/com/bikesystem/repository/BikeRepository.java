package com.bikesystem.repository;

import com.bikesystem.model.Bike;
import com.bikesystem.model.BikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BikeRepository extends JpaRepository<Bike, String> {

    Optional<Bike> findByBikeId(String bikeId);

    List<Bike> findByStatus(BikeStatus status);

    long countByStatus(BikeStatus status);
}
