package com.bikesystem.repository;

import com.bikesystem.model.Repairman;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairmanRepository extends JpaRepository<Repairman, Long> {

    List<Repairman> findByOnDuty(Boolean onDuty);
}
