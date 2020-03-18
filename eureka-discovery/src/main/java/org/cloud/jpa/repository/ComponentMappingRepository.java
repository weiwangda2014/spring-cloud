package org.cloud.jpa.repository;

import org.cloud.jpa.domain.ComponentMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComponentMappingRepository extends JpaRepository<ComponentMapping, Long> {
}