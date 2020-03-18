package org.cloud.jpa.repository;

import org.cloud.jpa.domain.Revision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface RevisionRepository extends JpaRepository<Revision, Long> {

    @Query("SELECT s FROM Revision s where validFrom <= ?1 and (validTo is null or validTo > ?1)")
    public List<Revision> findRevisions(Date date);

    @Query("SELECT s FROM Revision s where validTo is null")
    public List<Revision> findCurrentRevisions();
}