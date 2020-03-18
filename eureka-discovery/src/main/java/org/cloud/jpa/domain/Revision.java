package org.cloud.jpa.domain;

import org.cloud.jpa.domain.annotation.RevisionAnnotation;
import org.cloud.jpa.domain.component.Component;
import org.hibernate.annotations.Synchronize;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Entity
@Table(name = "revisions")
public class Revision extends AbstractPersistable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "COMPONENT_ID")
    private Component component;

    private Date validFrom = new Date();
    private Date validTo = null;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "revision")
    @MapKey(name = "name")
    private Map<String, RevisionAnnotation> annotations = new HashMap<>();

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
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

    public Map<String, RevisionAnnotation> getAnnotations() {
        return annotations;
    }

    public String getAnnotation(String name) {
        RevisionAnnotation annotation = annotations.get(name);
        return (annotation != null) ? annotation.getValue() : null;
    }

    public void setAnnotation(String name, String value) {
        RevisionAnnotation annotation = annotations.get(name);
        if (annotation != null)
            annotation.setValue(value);
        else {
            annotation = new RevisionAnnotation();
            annotation.setName(name);
            annotation.setValue(value);
            annotation.setRevision(this);
            annotations.put(name, annotation);
        }
    }
}
