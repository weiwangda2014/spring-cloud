package org.cloud.service;

import org.cloud.jpa.domain.UnmappedTrace;
import org.cloud.jpa.domain.enums.HttpMethod;
import org.cloud.jpa.repository.UnmappedTraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class UnmappedTraceService {

    List<UnmappedTrace> cachedUnmappedTraces;

    @Autowired
    private UnmappedTraceRepository unmappedTraceRepository;

    @PostConstruct
    private void initialize() {
        cachedUnmappedTraces = new java.util.concurrent.CopyOnWriteArrayList<>(unmappedTraceRepository.findAll());
    }

    public UnmappedTrace saveUnmappedTrace(UnmappedTrace unmappedTrace) {
        Assert.notNull(unmappedTrace);
        UnmappedTrace result = unmappedTraceRepository.saveAndFlush(unmappedTrace);
        cachedUnmappedTraces.add(result);
        return result;
    }

    public void deleteUnmappedTraces(List<UnmappedTrace> traces) {
        cachedUnmappedTraces.removeAll(traces);
        unmappedTraceRepository.delete(traces);
    }

    public List<UnmappedTrace> findAll() {
        return cachedUnmappedTraces;
    }

    public UnmappedTrace findByHttpPathAndMethod(String httpPath, HttpMethod httpMethod) {
        Assert.notNull(httpPath);
        Assert.notNull(httpMethod);
        for (UnmappedTrace unmappedTrace : cachedUnmappedTraces)
            if (unmappedTrace.getHttpPath().equals(httpPath) && unmappedTrace.getHttpMethod().equals(httpMethod))
                return unmappedTrace;

        return null;
    }
}
