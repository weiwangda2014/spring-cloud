package org.cloud.jpa.repository;

import org.cloud.jpa.domain.MappedSpan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MappedSpanRepository extends JpaRepository<MappedSpan, Long> {
}
