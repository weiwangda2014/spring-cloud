package org.cloud.jpa.domain;

import org.cloud.jpa.domain.enums.HttpMethod;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "traces_unmapped")
public class UnmappedTrace extends AbstractPersistable {

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

