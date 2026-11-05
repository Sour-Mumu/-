package com.bikesystem.repository;

import com.bikesystem.model.DispatchRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchRouteRepository extends JpaRepository<DispatchRoute, Long> {
}
