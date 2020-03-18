package org.cloud.jpa.repository.component;

import org.cloud.jpa.domain.component.Instance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    public List<Instance> findAll();

    public Instance findByName(String name);
}