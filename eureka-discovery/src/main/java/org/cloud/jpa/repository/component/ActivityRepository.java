package org.cloud.jpa.repository.component;

import org.cloud.jpa.domain.component.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}