package org.cloud.jpa.repository;

import org.cloud.jpa.domain.Relation;
import org.cloud.jpa.domain.Revision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RelationRepository extends JpaRepository<Relation, Long> {
    public Relation findByOwnerAndCallerAndCallee(Revision owner, Revision caller, Revision callee);
}