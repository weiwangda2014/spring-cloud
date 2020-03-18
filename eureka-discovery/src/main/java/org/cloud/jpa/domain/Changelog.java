package org.cloud.jpa.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "changelog")
@JsonIgnoreProperties({"id"})
public class Changelog extends AbstractPersistable {

    private Long referenceId = 0L;
    @Enumerated(EnumType.STRING)
    private ReferenceType referenceType;
    @Enumerated(EnumType.STRING)
    private Operation operation;
    private Date time = new Date();

    public enum ReferenceType {
        COMPONENT, REVISION, RELATION
    }

    public enum Operation {
        CREATED, UPDATED, DELETED
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(ReferenceType referenceType) {
        this.referenceType = referenceType;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}

