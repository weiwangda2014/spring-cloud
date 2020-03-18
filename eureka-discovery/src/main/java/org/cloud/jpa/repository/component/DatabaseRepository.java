package org.cloud.jpa.repository.component;

import org.cloud.jpa.domain.component.Database;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatabaseRepository extends JpaRepository<Database, Long> {
    public List<Database> findAll();

    public Database findByName(String name);
}