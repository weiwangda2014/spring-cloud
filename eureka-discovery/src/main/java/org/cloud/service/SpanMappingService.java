package org.cloud.service;

import com.google.common.net.InetAddresses;
import org.cloud.jpa.domain.MappedSpan;
import org.cloud.jpa.repository.MappedSpanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zipkin.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpanMappingService {

    private MappedSpanRepository mappedSpanRepository;
    private RevisionService revisionService;

    @Autowired
    public SpanMappingService(MappedSpanRepository mappedSpanRepository,
                              RevisionService revisionService) {
        this.mappedSpanRepository = mappedSpanRepository;
        this.revisionService = revisionService;
    }

    @Transactional
    public void mapAndSaveSpans(List<Span> spans) {
        List<MappedSpan> mappedSpans = map(spans);
        mappedSpanRepository.save(mappedSpans);
    }

    private List<MappedSpan> map(List<Span> spans) {
        List<MappedSpan> mappedSpans = new ArrayList<>();
        spans.forEach(span -> {
            String activity = revisionService.findActivityLabelByUrl(span.name);
            mappedSpans.addAll(
                    span.annotations.stream().map(annotation -> {
                        MappedSpan mappedSpan = map(span);
                        mappedSpan.setAnnotationValue(annotation.value);
                        mappedSpan.setEndpoint(InetAddresses.fromInteger(annotation.endpoint.ipv4).getHostAddress());
                        mappedSpan.setPort(Integer.valueOf(annotation.endpoint.port));
                        mappedSpan.setService(annotation.endpoint.serviceName);
                        mappedSpan.setActivity(activity);
                        return mappedSpan;
                    }).collect(Collectors.toList())
            );
            mappedSpans.addAll(
                    span.binaryAnnotations.stream().map(annotation -> {
                        MappedSpan mappedSpan = map(span);
                        mappedSpan.setAnnotationKey(annotation.key);
                        mappedSpan.setAnnotationValue(new String(annotation.value));
                        mappedSpan.setEndpoint(InetAddresses.fromInteger(annotation.endpoint.ipv4).getHostAddress());
                        mappedSpan.setPort(Integer.valueOf(annotation.endpoint.port));
                        mappedSpan.setService(annotation.endpoint.serviceName);
                        mappedSpan.setActivity(activity);
                        return mappedSpan;
                    }).collect(Collectors.toList())
            );
        });
        return mappedSpans;
    }

    private MappedSpan map(Span span) {
        MappedSpan mappedSpan = new MappedSpan();
        mappedSpan.setTraceIdHigh(span.traceIdHigh);
        mappedSpan.setTraceId(span.traceId);
        mappedSpan.setName(span.name);
        mappedSpan.setSpanId(span.id);
        mappedSpan.setParentId(span.parentId);
        mappedSpan.setTimestamp(span.timestamp);
        mappedSpan.setDuration(span.duration);
        return mappedSpan;
    }
}
