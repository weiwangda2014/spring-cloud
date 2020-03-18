package org.cloud.jpa.domain.annotation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloud.jpa.domain.component.Component;


import javax.persistence.*;

@Entity
@Table(name = "component_annotations")
public class ComponentAnnotation extends Annotation {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    @JsonIgnore
    private Component component = null;


    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

}