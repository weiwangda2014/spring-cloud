package org.cloud.service;

import org.cloud.jpa.domain.ModeledRelation;
import org.cloud.jpa.domain.Relation;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.component.Hardware;
import org.cloud.jpa.domain.component.Instance;
import org.cloud.jpa.repository.RelationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RelationService {

    List<Relation> cachedRelations;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private ChangelogService changelogService;

    @PostConstruct
    private void initialize() {
        cachedRelations = new java.util.concurrent.CopyOnWriteArrayList<>(relationRepository.findAll());
    }

    public Relation saveRelation(Relation relation) {

        //Add the time of discovery to new relations
        if (relation.getId() == null)
            relation.setAnnotation("ad.discovered_at", String.valueOf(new Date().getTime()));

        Assert.notNull(relation);
        if (relation.getId() != null)
            cachedRelations.remove(relation);
        Relation result = (Relation) changelogService.saveAndLogPersistable(relation, relationRepository);
        cachedRelations.add(result);
        return result;
    }

    public List<Relation> findAll() {
        return cachedRelations;
    }

    public Relation findByOwnerAndCallerAndCallee(Revision owner, Revision caller, Revision callee) {
        Assert.notNull(owner);
        Assert.notNull(caller);
        Assert.notNull(callee);

        Relation relation = new Relation();
        relation.setOwner(owner);
        relation.setCaller(caller);
        relation.setCallee(callee);

        List<Relation> relations = cachedRelations.stream().filter(s -> s.equals(relation)).collect(Collectors.toList());
        return relations.size() > 0 ? relations.get(0) : null;
    }

    public List<Relation> findByOwner(Revision owner) {
        Assert.notNull(owner);

        return cachedRelations.stream().filter(s -> s.getOwner().getId().equals(owner.getId())).collect(Collectors.toList());
    }

    public List<Relation> findByCallee(Revision callee) {
        Assert.notNull(callee);

        return cachedRelations.stream().filter(s -> s.getCallee().getId().equals(callee.getId())).collect(Collectors.toList());
    }

    public Relation findById(Long id) {
        Assert.notNull(id);
        for (Relation relation : cachedRelations)
            if (relation.getId().equals(id))
                return relation;
        return null;
    }

    public void setInstanceRevisionRelations(Revision instanceRevision, Revision serviceRevision, Revision hardwareRevision) {
        Assert.isTrue(instanceRevision.getComponent() instanceof Instance);
        Assert.isTrue(serviceRevision.getComponent() instanceof org.cloud.jpa.domain.component.Service);
        Assert.isTrue(hardwareRevision.getComponent() instanceof Hardware);

        Relation serviceRelation = new Relation();
        serviceRelation.setCaller(serviceRevision);
        serviceRelation.setCallee(instanceRevision);
        serviceRelation.setOwner(serviceRelation.getCaller());
        saveRelation(serviceRelation);

        Relation hardwareRelation = new Relation();
        hardwareRelation.setCaller(instanceRevision);
        hardwareRelation.setCallee(hardwareRevision);
        hardwareRelation.setOwner(hardwareRelation.getCaller());
        saveRelation(hardwareRelation);

        Relation serviceHardwareRelation = new Relation();
        serviceHardwareRelation.setCaller(instanceRevision);
        serviceHardwareRelation.setCallee(hardwareRevision);
        serviceHardwareRelation.setOwner(serviceRevision);
        saveRelation(serviceHardwareRelation);
    }
}
