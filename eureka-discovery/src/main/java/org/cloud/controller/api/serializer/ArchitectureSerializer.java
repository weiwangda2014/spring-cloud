package org.cloud.controller.api.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.cloud.controller.api.model.Architecture;
import org.cloud.controller.api.model.RelationFilter;
import org.cloud.jpa.domain.ModeledRelation;
import org.cloud.jpa.domain.Relation;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.annotation.Annotation;
import org.cloud.jpa.domain.component.Component;
import org.cloud.service.ModeledRelationService;
import org.cloud.service.RelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ArchitectureSerializer extends StdSerializer<Architecture> {

    @Autowired
    private RelationService relationService;

    @Autowired
    private ModeledRelationService modeledRelationService;

    public ArchitectureSerializer() {
        this(null);
    }

    public ArchitectureSerializer(Class<Architecture> t) {
        super(t);
    }

    @Value("${ad.annotations.default.suppressed}")
    private String suppressedAnnotationsOnDefault;

    @Override
    public void serialize(Architecture architecture, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        Map<Long, Revision> revisionMap = new HashMap<>();
        for (Revision revision : architecture.getRevisions())
            revisionMap.put(revision.getId(), revision);

        jgen.writeStartObject();
        jgen.writeObjectField("snapshot", (architecture.getSnapshot() == null) ? new Date() : architecture.getSnapshot());
        jgen.writeFieldName("components");
        jgen.writeStartObject();
        for (Long revisionId : revisionMap.keySet()) {
            jgen.writeFieldName(revisionId.toString());
            serializeRevision(architecture, revisionMap.get(revisionId), jgen);
        }
        jgen.writeEndObject();
        jgen.writeObjectField("root-components", architecture.getRootRevisionIds());
        jgen.writeEndObject();
    }

    public void serializeRevision(Architecture architecture, Revision revision, JsonGenerator jgen) throws IOException {
        Date snapshot = architecture.getSnapshot();

        jgen.writeStartObject();
        jgen.writeFieldName("component");
        serializeComponent(architecture, revision.getComponent(), jgen);

        addAnnotations(architecture, jgen, new ArrayList<>(revision.getAnnotations().values()));

        Set<Long> currentRevisions = architecture.getRevisionIds();

        if (validDirection(architecture, RelationFilter.CHILDREN)) {
            List<ModeledRelation> modeledChildRelations = (snapshot == null) ? modeledRelationService.findByCaller(revision) : modeledRelationService.findByCaller(revision, snapshot);
            List<Relation> childRelations = relationService.findByOwner(revision).stream().filter(r -> currentRevisions.contains(r.getCaller().getId()) && currentRevisions.contains(r.getCallee().getId())).collect(Collectors.toList());
            if (childRelations.size() > 0 || modeledChildRelations.size() > 0) {
                jgen.writeFieldName("child-relations");
                jgen.writeStartArray();
                for (ModeledRelation relation : modeledChildRelations) {
                    jgen.writeStartObject();
                    jgen.writeNumberField("callee", relation.getCallee().getId());
                    addAnnotations(architecture, jgen, new ArrayList<>(relation.getAnnotations().values()));
                    jgen.writeEndObject();
                }

                for (Relation relation : childRelations) {
                    jgen.writeStartObject();
                    if (relation.getCaller() != relation.getOwner())
                        jgen.writeNumberField("caller", relation.getCaller().getId());
                    jgen.writeNumberField("callee", relation.getCallee().getId());
                    addAnnotations(architecture, jgen, new ArrayList<>(relation.getAnnotations().values()));
                    jgen.writeEndObject();
                }
                jgen.writeEndArray();
            }
        }


        List<ModeledRelation> modeledParentRelations = (snapshot == null) ? modeledRelationService.findByCallee(revision) : modeledRelationService.findByCallee(revision, snapshot);
        List<Relation> parentRelations = relationService.findByCallee(revision).stream().filter(r -> currentRevisions.contains(r.getCaller().getId()) && currentRevisions.contains(r.getOwner().getId())).collect(Collectors.toList());
        if (validDirection(architecture, RelationFilter.PARENTS) && (parentRelations.size() > 0 || modeledParentRelations.size() > 0)) {
            jgen.writeFieldName("parent-relations");
            jgen.writeStartArray();
            for (ModeledRelation relation : modeledParentRelations) {
                jgen.writeStartObject();
                jgen.writeNumberField("caller", relation.getCaller().getId());
                addAnnotations(architecture, jgen, new ArrayList<>(relation.getAnnotations().values()));
                jgen.writeEndObject();
            }

            for (Relation relation : parentRelations) {
                jgen.writeStartObject();
                jgen.writeNumberField("caller", relation.getCaller().getId());
                if (relation.getCaller() != relation.getOwner())
                    jgen.writeNumberField("owner", relation.getOwner().getId());
                addAnnotations(architecture, jgen, new ArrayList<>(relation.getAnnotations().values()));
                jgen.writeEndObject();
            }
            jgen.writeEndArray();
        }
        jgen.writeEndObject();

        if (modeledParentRelations.size() == 0 && parentRelations.size() == 0)
            architecture.addRootRevisionId(revision.getId());
    }

    private void serializeComponent(Architecture architecture, Component component, JsonGenerator jgen) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", component.getId());
        jgen.writeStringField("type", component.getClass().getSimpleName());
        jgen.writeObjectField("name", component.getName());
        addAnnotations(architecture, jgen, new ArrayList<>(component.getAnnotations().values()));
        jgen.writeEndObject();
    }

    private boolean validDirection(Architecture architecture, RelationFilter direction) {
        RelationFilter filter = architecture.getRelationFilter();
        return (filter.equals(RelationFilter.ALL) || direction.equals(filter));
    }

    private void addAnnotations(Architecture architecture, JsonGenerator jgen, List<Annotation> annotations) throws IOException, JsonProcessingException {
        List<String> filter = architecture.getAnnotationFilters();
        if (!filter.contains("NONE") && annotations.size() > 0) {
            List<Annotation> filteredAnnotations;
            if (filter.size() > 0) {
                filteredAnnotations = new ArrayList<>();
                for (Annotation annotation : annotations)
                    if (filter.contains(annotation.getName()))
                        filteredAnnotations.add(annotation);
            } else {
                List<String> negativeFilter = Arrays.asList(suppressedAnnotationsOnDefault.split(","));
                filteredAnnotations = new ArrayList<>();
                for (Annotation annotation : annotations)
                    if (!negativeFilter.contains(annotation.getName()))
                        filteredAnnotations.add(annotation);
            }

            if (filteredAnnotations.size() > 0) {
                jgen.writeFieldName("annotations");
                jgen.writeStartObject();
                for (Annotation annotation : filteredAnnotations)
                    jgen.writeObjectField(annotation.getName(), annotation.getValue());
                jgen.writeEndObject();
            }
        }
    }
}