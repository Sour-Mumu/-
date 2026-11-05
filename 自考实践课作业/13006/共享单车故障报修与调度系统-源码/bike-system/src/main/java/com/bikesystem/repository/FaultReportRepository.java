package com.bikesystem.repository;

import com.bikesystem.model.FaultReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaultReportRepository extends JpaRepository<FaultReport, Long> {

    List<FaultReport> findByDispatched(Boolean dispatched);

    List<FaultReport> findByBikeId(String bikeId);
}
