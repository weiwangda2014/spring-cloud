package org.cloud.jpa.repository.component;

import org.cloud.jpa.domain.component.Service;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    public List<Service> findAll();

    public Service findByName(String name);
}