package org.cloud.jpa.domain.annotation;

import org.cloud.jpa.domain.Revision;

import javax.persistence.*;


@Entity
@Table(name = "revision_annotations")
public class RevisionAnnotation extends Annotation {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revision_id", nullable = false)
    private Revision revision = null;

    public Revision getRevision() {
        return revision;
    }

    public void setRevision(Revision revision) {
        this.revision = revision;
    }
}