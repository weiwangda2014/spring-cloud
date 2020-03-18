package org.cloud.jpa.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "spans_mapped")
public class MappedSpan extends AbstractPersistable {
    private Long traceIdHigh;
    private Long traceId;
    private String name;
    private Long spanId;
    private Long parentId;
    private Long timestamp;
    private Long duration;

    private String annotationKey;
    private String annotationValue;
    private String endpoint;
    private Integer port;
    private String service;
    private String activity;

    public Long getTraceIdHigh() {
        return traceIdHigh;
    }

    public void setTraceIdHigh(Long traceIdHigh) {
        this.traceIdHigh = traceIdHigh;
    }

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSpanId() {
        return spanId;
    }

    public void setSpanId(Long spanId) {
        this.spanId = spanId;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getAnnotationKey() {
        return annotationKey;
    }

    public void setAnnotationKey(String annotationKey) {
        this.annotationKey = annotationKey;
    }

    public String getAnnotationValue() {
        return annotationValue;
    }

    public void setAnnotationValue(String annotationValue) {
        this.annotationValue = annotationValue;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
