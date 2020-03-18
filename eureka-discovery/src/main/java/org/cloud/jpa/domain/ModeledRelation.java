package org.cloud.jpa.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.cloud.jpa.domain.annotation.ModeledRelationAnnotation;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "modeledrelations")
public class ModeledRelation extends AbstractPersistable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Revision caller = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "callee", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Revision callee = null;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date validFrom = new Date();

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date validTo = null;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "modeledRelation")
    @MapKey(name = "name")
    private Map<String, ModeledRelationAnnotation> annotations = new HashMap<>();

    public Revision getCaller() {
        return caller;
    }

    public void setCaller(Revision caller) {
        this.caller = caller;
    }

    public Revision getCallee() {
        return callee;
    }

    public void setCallee(Revision callee) {
        this.callee = callee;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public Map<String, ModeledRelationAnnotation> getAnnotations() {
        return annotations;
    }

    public String getAnnotation(String name) {
        ModeledRelationAnnotation annotation = annotations.get(name);
        return (annotation != null) ? annotation.getValue() : null;
    }

    public void setAnnotation(String name, String value) {
        ModeledRelationAnnotation annotation = annotations.get(name);
        if (annotation != null)
            annotation.setValue(value);
        else {
            annotation = new ModeledRelationAnnotation();
            annotation.setName(name);
            annotation.setValue(value);
            annotation.setModeledRelation(this);
            annotations.put(name, annotation);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof ModeledRelation)) return false;
        ModeledRelation otherRelationClass = (ModeledRelation) other;
        if (otherRelationClass.caller.getId().equals(this.caller.getId()) && otherRelationClass.callee.getId().equals(this.callee.getId()))
            return true;

        return false;
    }
}

