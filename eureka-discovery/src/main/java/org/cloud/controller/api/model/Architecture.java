package org.cloud.controller.api.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.cloud.controller.api.serializer.ArchitectureSerializer;
import org.cloud.jpa.domain.Revision;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonSerialize(using = ArchitectureSerializer.class)
public class Architecture {

    private Date snapshot;
    private List<Revision> revisions;
    private Set<Long> revisionIds = new HashSet<>();
    private Set<Long> rootRevisionIds = new HashSet<>();
    private RelationFilter relationFilter;
    private List<String> annotationFilters;

    public Date getSnapshot() {
        return snapshot;
    }

    public void setSnapshot(Date snapshot) {
        this.snapshot = snapshot;
    }

    public List<Revision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<Revision> revisions) {
        this.revisions = revisions;
        for (Revision rev : revisions)
            revisionIds.add(rev.getId());
    }

    public Set<Long> getRevisionIds() {
        return revisionIds;
    }

    public Set<Long> getRootRevisionIds() {
        return rootRevisionIds;
    }

    public void addRootRevisionId(Long id) {
        this.rootRevisionIds.add(id);
    }

    public RelationFilter getRelationFilter() {
        return relationFilter;
    }

    public void setRelationFilter(RelationFilter relationFilter) {
        this.relationFilter = relationFilter;
    }

    public List<String> getAnnotationFilters() {
        return annotationFilters;
    }

    public void setAnnotationFilters(List<String> annotationFilters) {
        this.annotationFilters = annotationFilters;
    }
}
