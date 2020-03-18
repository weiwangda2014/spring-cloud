package org.cloud.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import org.cloud.controller.SpanController;
import org.cloud.jpa.domain.ComponentMapping;
import org.cloud.jpa.domain.UnmappedTrace;
import org.cloud.service.ComponentMappingService;
import org.cloud.service.UnmappedTraceService;
import org.cloud.service.component.ComponentService;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zipkin.Span;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/v1/ad/component-mapping")
@Api(value = "architecture-discovery-api-v1", description = "Architecture Discovery Api v1endpoints")
public class ComponentMappingController {

    @Autowired
    private SpanController spanController;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentMappingService componentMappingService;

    @Autowired
    private UnmappedTraceService unmappedTraceService;

    @RequestMapping(method = RequestMethod.GET)
    public List<ComponentMapping> getComponentMappings() throws JsonProcessingException {
        return componentMappingService.findAll();
    }

    @RequestMapping(method = RequestMethod.POST)
    public ComponentMapping createComponentMapping(@RequestBody ComponentMapping componentMapping) throws Exception {
        componentMapping.setComponent(componentService.findById(componentMapping.getComponent().getId()));

        if (componentMapping.getHttpMethods() < 1 || componentMapping.getHttpMethods() > 15)
            throw new IllegalArgumentException("HttpMethods must be within 1 - 15");

        if (componentMapping.getComponent() == null)
            throw new NotFoundException("Component was not found!");

        if (componentMappingService.findAll().contains(componentMapping))
            throw new IllegalArgumentException("This mapping exists already");

        componentMapping = componentMappingService.saveComponentMapping(componentMapping);

        // map all now mappable traces
        List<UnmappedTrace> tracesToRemove = new ArrayList<>();
        for (UnmappedTrace trace : unmappedTraceService.findAll()) {
            if ((componentMapping.getHttpMethods() & trace.getHttpMethod().getValue()) != 0 && Pattern.compile(componentMapping.getHttpPathRegex()).matcher(trace.getHttpPath()).find()) {
                List<Span> spans = spanController.getZipkinStorageComponent().spanStore().getRawTrace(trace.getTraceId());
                if (spans != null && spans.size() > 0)
                    spanController.proceedSpans(spans);
                tracesToRemove.add(trace);
            }
        }

        if (tracesToRemove.size() > 0)
            unmappedTraceService.deleteUnmappedTraces(tracesToRemove);

        return componentMapping;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ComponentMapping getComponentMapping(@PathVariable Long id) throws Exception {
        ComponentMapping mapping = componentMappingService.findById(id);
        if (mapping == null)
            throw new NotFoundException("ComponentMapping not found");
        return mapping;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteComponentMapping(@PathVariable Long id) throws Exception {
        componentMappingService.delete(getComponentMapping(id));
    }
}
