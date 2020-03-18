package org.cloud.service;

import org.cloud.jpa.domain.Changelog;
import org.cloud.jpa.domain.ModeledRelation;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.repository.ModeledRelationRepository;
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
public class ModeledRelationService {

    List<ModeledRelation> cachedModeledRelations;

    @Autowired
    private ModeledRelationRepository modeledRelationRepository;

    @Autowired
    private ChangelogService changelogService;

    @PostConstruct
    private void initialize() {
        cachedModeledRelations = new java.util.concurrent.CopyOnWriteArrayList<>(modeledRelationRepository.findAll());
    }

    public ModeledRelation saveModeledRelation(ModeledRelation modeledRelation) {
        Assert.notNull(modeledRelation);
        ModeledRelation result = (ModeledRelation) changelogService.saveAndLogPersistable(modeledRelation, modeledRelationRepository);
        cachedModeledRelations.add(result);
        return result;
    }

    public List<ModeledRelation> findAll() {
        return cachedModeledRelations;
    }

    public List<ModeledRelation> findAllCurrent() {
        return cachedModeledRelations.stream().filter(s -> s.getValidTo() == null).collect(Collectors.toList());
    }

    public List<ModeledRelation> findByCallee(Revision callee) {
        Assert.notNull(callee);

        return cachedModeledRelations.stream().filter(s -> s.getCallee().getId().equals(callee.getId()) && s.getValidTo() == null).collect(Collectors.toList());
    }

    public List<ModeledRelation> findByCallee(Revision callee, Date snapshot) {
        Assert.notNull(callee);
        Assert.notNull(snapshot);
        return cachedModeledRelations.stream().filter(s -> s.getCallee().getId().equals(callee.getId()) && s.getValidFrom().before(snapshot) && (s.getValidTo() == null || s.getValidTo().after(snapshot))).collect(Collectors.toList());
    }

    public List<ModeledRelation> findByCaller(Revision caller) {
        Assert.notNull(caller);

        return cachedModeledRelations.stream().filter(s -> s.getCaller().getId().equals(caller.getId()) && s.getValidTo() == null).collect(Collectors.toList());
    }

    public List<ModeledRelation> findByCaller(Revision caller, Date snapshot) {
        Assert.notNull(caller);
        Assert.notNull(snapshot);
        return cachedModeledRelations.stream().filter(s -> s.getCaller().getId().equals(caller.getId()) && s.getValidFrom().before(snapshot) && (s.getValidTo() == null || s.getValidTo().after(snapshot))).collect(Collectors.toList());
    }

    public ModeledRelation findByCallerAndCallee(Revision caller, Revision callee) {
        Assert.notNull(caller);
        Assert.notNull(callee);
        for (ModeledRelation relation : cachedModeledRelations)
            if (relation.getValidTo() == null && relation.getCaller().getId().equals(caller.getId()) && relation.getCallee().getId().equals(callee.getId()))
                return relation;
        return null;
    }

    public ModeledRelation findByCallerAndCallee(Revision caller, Revision callee, Date snapshot) {
        Assert.notNull(caller);
        Assert.notNull(callee);
        Assert.notNull(snapshot);
        for (ModeledRelation relation : cachedModeledRelations)
            if (relation.getValidTo() == null && relation.getCaller().getId().equals(caller.getId()) && relation.getCallee().getId().equals(callee.getId()) && relation.getValidFrom().before(snapshot) && (relation.getValidTo() == null || relation.getValidTo().after(snapshot)))
                return relation;
        return null;
    }

    public ModeledRelation closeModeledRelation(ModeledRelation relation) {
        Assert.notNull(relation);
        Assert.isNull(relation.getValidTo());
        relation.setValidTo(new Date());
        cachedModeledRelations.remove(relation);
        relation = (ModeledRelation) changelogService.saveAndLogPersistable(relation, modeledRelationRepository, Changelog.Operation.DELETED);
        cachedModeledRelations.add(relation);
        return relation;
    }

    public ModeledRelation findById(Long id) {
        Assert.notNull(id);
        for (ModeledRelation relation : cachedModeledRelations)
            if (relation.getId().equals(id))
                return relation;
        return null;
    }
}
