package org.cloud.service.component;

import org.cloud.jpa.domain.component.Component;
import org.cloud.service.ChangelogService;
import org.cloud.service.RevisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ComponentBaseService<T extends Component> {

    private JpaRepository repository;
    protected List<T> cachedComponents;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ChangelogService changelogService;

    @Autowired
    private RevisionService revisionService;

    protected void initialize(JpaRepository<T, Long> repository) {
        this.repository = repository;
        cachedComponents = new java.util.concurrent.CopyOnWriteArrayList<>(repository.findAll());
        componentService.registerComponents(cachedComponents);
    }

    public List<T> findAll() {
        return cachedComponents;
    }

    public T findById(Long id) {
        Assert.notNull(id);
        for (T component : cachedComponents)
            if (component.getId().equals(id))
                return component;
        return null;
    }

    public T findByName(String name) {
        Assert.notNull(name);
        for (T componend : cachedComponents)
            if (componend.getName().equals(name))
                return componend;
        return null;
    }

    protected T createComponent(T component, String name) {
        component.setName(name);
        component.setAnnotation("ad.discovered_at", String.valueOf(new Date().getTime()));
        component = saveComponent(component);
        revisionService.createRevision(component);
        return component;
    }

    protected T saveComponent(T component) {
        Assert.notNull(component);
        if (component.getId() != null) {
            cachedComponents.remove(component);
            componentService.unregisterComponent(component);
        }
        component = (T) changelogService.saveAndLogPersistable(component, repository);
        cachedComponents.add(component);
        componentService.registerComponent(component);
        return component;
    }
}
