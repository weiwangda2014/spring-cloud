package org.cloud.jpa.domain.component;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cloud.jpa.domain.AbstractPersistable;
import org.cloud.jpa.domain.ComponentMapping;
import org.cloud.jpa.domain.annotation.ComponentAnnotation;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Entity
@Table(name = "components")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Component extends AbstractPersistable {
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "component")
    @MapKey(name = "name")
    private Map<String, ComponentAnnotation> annotations = new HashMap<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "component")
    private List<ComponentMapping> mappings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, ComponentAnnotation> getAnnotations() {
        return annotations;
    }

    public String getAnnotation(String name) {
        ComponentAnnotation annotation = annotations.get(name);
        return (annotation != null) ? annotation.getValue() : null;
    }

    public void setAnnotation(String name, String value) {
        ComponentAnnotation annotation = annotations.get(name);
        if (annotation != null)
            annotation.setValue(value);
        else {
            annotation = new ComponentAnnotation();
            annotation.setName(name);
            annotation.setValue(value);
            annotation.setComponent(this);
            annotations.put(name, annotation);
        }
    }

    public List<ComponentMapping> getMappings() {
        return mappings;
    }
}
