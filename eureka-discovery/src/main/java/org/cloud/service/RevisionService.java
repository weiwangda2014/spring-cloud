package org.cloud.service;

import org.cloud.jpa.domain.Changelog;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.component.Component;
import org.cloud.jpa.repository.RevisionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class RevisionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RevisionService.class);

    @Autowired
    private RevisionRepository componentRevisionRepository;

    @Autowired
    private ChangelogService changelogService;

    private Map<String, Revision> currentRevisionsByComponentName = new ConcurrentHashMap<>();
    private Map<Long, Revision> currentRevisionsByComponentId = new ConcurrentHashMap<>();

    @PostConstruct
    private void Initialize() {
        for (Revision revision : componentRevisionRepository.findCurrentRevisions()) {
            currentRevisionsByComponentName.put(revision.getComponent().getName(), revision);
            currentRevisionsByComponentId.put(revision.getComponent().getId(), revision);
        }
    }

    public Map<String, Revision> getCurrentRevisionsByComponentName() {
        return currentRevisionsByComponentName;
    }

    public Map<Long, Revision> getCurrentRevisionsByComponentId() {
        return currentRevisionsByComponentId;
    }

    public List<Revision> getRevisionsByDate(Date snapshot) {
        return componentRevisionRepository.findRevisions(snapshot);
    }

    public Revision createRevision(Component component) {
        Revision revision = new Revision();
        revision.setComponent(component);
        revision.setValidFrom(new Date());
        saveRevision(revision);
        currentRevisionsByComponentName.put(component.getName(), revision);
        currentRevisionsByComponentId.put(component.getId(), revision);
        return revision;
    }

    public Revision closeRevision(Revision revision) {
        Assert.isNull(revision.getValidTo());
        revision.setValidTo(new Date());
        currentRevisionsByComponentName.remove(revision.getComponent().getName());
        currentRevisionsByComponentId.remove(revision.getComponent().getId());
        return (Revision) changelogService.saveAndLogPersistable(revision, componentRevisionRepository, Changelog.Operation.DELETED);
    }

    public Revision saveRevision(Revision revision) {
        Assert.notNull(revision);
        currentRevisionsByComponentName.remove(revision.getComponent().getName());
        currentRevisionsByComponentId.remove(revision.getComponent().getId());
        revision = (Revision) changelogService.saveAndLogPersistable(revision, componentRevisionRepository);
        currentRevisionsByComponentName.put(revision.getComponent().getName(), revision);
        currentRevisionsByComponentId.put(revision.getComponent().getId(), revision);
        return revision;
    }

    public Revision findById(Long id) {
        Assert.notNull(id);
        for (Revision revision : currentRevisionsByComponentId.values())
            if (revision.getId().equals(id))
                return revision;
        return componentRevisionRepository.findOne(id);
    }

    public String findActivityLabelByUrl(String url) {
        // can't be filtered with a jpql query because regexp matching isn't supported
        List<Revision> revisions = componentRevisionRepository.findCurrentRevisions()
                .stream()
                .filter(
                        revision -> revision.getComponent().getMappings()
                                .stream()
                                .anyMatch(componentMapping -> {
                                    Pattern pattern = Pattern.compile(componentMapping.getHttpPathRegex());
                                    Matcher matcher = pattern.matcher(url);
                                    return matcher.find();
                                })
                )
                .collect(Collectors.toList());
        if (revisions.isEmpty()) {
            LOGGER.error("Failed to find any revision for url={}", url);
            return null;
        }
        if (revisions.size() != 1) {
            LOGGER.error("Failed to find a unique revision for url={}", url);
        }
        return revisions.get(0).getAnnotation("ad.model.label");
    }
}
