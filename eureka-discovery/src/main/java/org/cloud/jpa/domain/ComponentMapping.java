package org.cloud.jpa.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.cloud.jpa.domain.component.Component;
import org.cloud.service.component.ComponentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;

@Entity
@Table(name = "trace_component_mapping")
public class ComponentMapping extends AbstractPersistable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    private Component component = null;
    private String httpPathRegex = "";
    private Integer httpMethods = 0;

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    @JsonProperty("component")
    public void setComponent(Long componentId) {
        this.component = new Component();
        this.component.setId(componentId);
    }

    public String getHttpPathRegex() {
        return httpPathRegex;
    }

    public void setHttpPathRegex(String httpPathRegex) {
        this.httpPathRegex = httpPathRegex;
    }

    public Integer getHttpMethods() {
        return httpMethods;
    }

    public void setHttpMethods(Integer httpMethods) {
        this.httpMethods = httpMethods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentMapping that = (ComponentMapping) o;
        if (this.getId() != null && this.getId().equals(that.getId())) return true;

        if (!component.getId().equals(that.component.getId())) return false;
        if (!httpPathRegex.equals(that.httpPathRegex)) return false;
        return httpMethods.equals(that.httpMethods);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + component.getId().hashCode();
        result = 31 * result + httpPathRegex.hashCode();
        result = 31 * result + httpMethods.hashCode();
        return result;
    }
}

