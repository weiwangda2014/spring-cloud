package org.cloud.service;

import org.cloud.jpa.domain.ComponentMapping;
import org.cloud.jpa.repository.ComponentMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ComponentMappingService {

    List<ComponentMapping> cachedComponentMappings;

    @Autowired
    private ComponentMappingRepository componentMappingRepository;

    @PostConstruct
    private void initialize() {
        cachedComponentMappings = new java.util.concurrent.CopyOnWriteArrayList<>(componentMappingRepository.findAll());
    }

    public ComponentMapping saveComponentMapping(ComponentMapping componentMapping) {
        Assert.notNull(componentMapping);
        ComponentMapping result = componentMappingRepository.saveAndFlush(componentMapping);
        cachedComponentMappings.add(result);
        return result;
    }

    public List<ComponentMapping> findAll() {
        return cachedComponentMappings;
    }

    public ComponentMapping findById(long id) {
        Assert.notNull(id);
        for (ComponentMapping mapping : cachedComponentMappings)
            if (mapping.getId().equals(id))
                return mapping;
        return null;
    }

    public void delete(List<ComponentMapping> componentMappings) {
        cachedComponentMappings.removeAll(componentMappings);
        componentMappingRepository.delete(componentMappings);
    }

    public void delete(ComponentMapping componentMapping) {
        cachedComponentMappings.remove(componentMapping);
        componentMappingRepository.delete(componentMapping);
    }
}
