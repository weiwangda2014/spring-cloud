package org.cloud.jpa.repository;

import org.cloud.jpa.domain.UnmappedTrace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnmappedTraceRepository extends JpaRepository<UnmappedTrace, Long> {
}