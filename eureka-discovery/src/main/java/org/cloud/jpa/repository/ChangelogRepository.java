package org.cloud.jpa.repository;

import org.cloud.jpa.domain.Changelog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChangelogRepository extends JpaRepository<Changelog, Long> {
}