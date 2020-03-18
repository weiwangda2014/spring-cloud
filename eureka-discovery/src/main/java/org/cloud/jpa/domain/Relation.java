package org.cloud.jpa.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.cloud.jpa.domain.annotation.RelationAnnotation;
import zipkin.BinaryAnnotation;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "relations")
public class Relation extends AbstractPersistable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Revision owner = null;

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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "relation")
    @MapKey(name = "name")
    private Map<String, RelationAnnotation> annotations = new HashMap<>();

    public Revision getOwner() {
        return owner;
    }

    public void setOwner(Revision owner) {
        this.owner = owner;
    }

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

    public Map<String, RelationAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotationsFromBinaryAnnotations(List<BinaryAnnotation> binaryAnnotations) {
        for (BinaryAnnotation binaryAnnotation : binaryAnnotations)
            setAnnotation(binaryAnnotation.key, new String(binaryAnnotation.value));
    }

    public Boolean annotationsRequireSave() {
        for (RelationAnnotation annotation : annotations.values())
            if (annotation.getId() == null)
                return true;

        return false;
    }

    public void setAnnotations(Map<String, RelationAnnotation> relationAnnotations) {
        for (RelationAnnotation relationAnnotation : relationAnnotations.values())
            setAnnotation(relationAnnotation.getName(), relationAnnotation.getValue());
    }

    public String getAnnotation(String name) {
        RelationAnnotation annotation = annotations.get(name);
        return (annotation != null) ? annotation.getValue() : null;
    }

    public void setAnnotation(String name, String value) {
        RelationAnnotation annotation = annotations.get(name);
        if (annotation != null)
            annotation.setValue(value);
        else {
            annotation = new RelationAnnotation();
            annotation.setName(name);
            annotation.setValue(value);
            annotation.setRelation(this);
            annotations.put(name, annotation);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Relation)) return false;
        Relation otherRelationClass = (Relation) other;
        if (otherRelationClass.owner.getId().equals(this.owner.getId()) && otherRelationClass.caller.getId().equals(this.caller.getId()) && otherRelationClass.callee.getId().equals(this.callee.getId()))
            return true;

        return false;
    }
}

