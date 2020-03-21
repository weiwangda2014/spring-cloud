package org.cloud.provider.repository;

import org.cloud.provider.entity.Trips;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TripsRepository extends JpaRepository<Trips, String>, JpaSpecificationExecutor<Trips> {
}
