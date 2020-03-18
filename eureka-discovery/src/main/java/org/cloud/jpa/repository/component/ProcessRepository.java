package org.cloud.jpa.repository.component;

import org.cloud.jpa.domain.component.Process;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepository extends JpaRepository<Process, Long> {
}