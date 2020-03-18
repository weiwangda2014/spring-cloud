package org.cloud.controller.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ModeledRelationPair {
    private Long callerId;
    private Long calleeId;

    public ModeledRelationPair() {
    }

    public ModeledRelationPair(Long callerId, Long calleeId) {
        this.callerId = callerId;
        this.calleeId = calleeId;
    }

    public Long getCallerId() {
        return callerId;
    }

    @JsonProperty(required = true)
    public void setCallerId(Long callerId) {
        this.callerId = callerId;
    }

    public Long getCalleeId() {
        return calleeId;
    }

    @JsonProperty(required = true)
    public void setCalleeId(Long calleeId) {
        this.calleeId = calleeId;
    }
}
