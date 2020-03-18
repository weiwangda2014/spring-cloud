package org.cloud.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cloud.jpa.domain.*;
import org.cloud.jpa.domain.component.Component;
import org.cloud.jpa.repository.ChangelogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@EnableBinding(Source.class)
public class ChangelogService {

    private List<Changelog> cachedChangelogs;

    @Autowired
    private ChangelogRepository changelogRepository;

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private Source source;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    private void initialize() {
        cachedChangelogs = new java.util.concurrent.CopyOnWriteArrayList<>(changelogRepository.findAll());
    }

    public AbstractPersistable saveAndLogPersistable(AbstractPersistable reference, JpaRepository repository) {
        Changelog.Operation operation = (reference.getId() == null) ? Changelog.Operation.CREATED : Changelog.Operation.UPDATED;
        return saveAndLogPersistable(reference, repository, operation);
    }

    public AbstractPersistable saveAndLogPersistable(AbstractPersistable reference, JpaRepository repository, Changelog.Operation operation) {
        reference = (AbstractPersistable) repository.saveAndFlush(reference);
        Changelog changelog = new Changelog();
        changelog.setReferenceId(reference.getId());
        if (reference instanceof Relation || reference instanceof ModeledRelation)
            changelog.setReferenceType(Changelog.ReferenceType.RELATION);
        else if (reference instanceof Revision)
            changelog.setReferenceType(Changelog.ReferenceType.REVISION);
        else if (reference instanceof Component)
            changelog.setReferenceType(Changelog.ReferenceType.COMPONENT);

        Assert.isTrue(changelog.getReferenceType() != null);

        changelog.setOperation(operation);
        if (!operation.equals(Changelog.Operation.UPDATED))
            saveChangelog(changelog);

        return reference;
    }

    public List<Changelog> findAll() {
        return cachedChangelogs;
    }

    private Changelog saveChangelog(Changelog changelog) {
        Assert.notNull(changelog);
        Changelog result = changelogRepository.saveAndFlush(changelog);
        cachedChangelogs.add(result);

        try {
            source.output().send(MessageBuilder.withPayload(objectMapper.writeValueAsString(result)).build());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return result;
    }
}
