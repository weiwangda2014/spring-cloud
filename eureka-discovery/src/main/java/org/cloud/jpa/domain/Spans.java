package org.cloud.jpa.domain;


import org.cloud.jpa.domain.annotation.Annotation;
import org.cloud.jpa.domain.enums.HttpMethod;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/*

@Entity
@Table(name = "zipkin_spans")
public class Spans extends AbstractPersistable {
    @JoinColumn(name="id", nullable=false)
    @OneToMany(cascade = CascadeType.ALL, fetch=FetchType.LAZY, mappedBy="annotations")
    @MapKey(name = "name")
    private Map<Long, Annotations> annotations = new HashMap<>();

    public Map<Long, Annotations> getAnnotations(){return this.annotations;}

    private Long traceId = 0L;
    private String httpPath = "";
    private Integer httpMethod = HttpMethod.GET.getValue();
    private Date time = new Date();

    public Long getTraceId() {
        return traceId;
    }

    public void setTraceId(Long traceId) {
        this.traceId = traceId;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.fromInt(httpMethod);
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod.getValue();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
*/
