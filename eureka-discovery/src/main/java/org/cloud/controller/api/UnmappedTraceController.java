package org.cloud.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import org.cloud.jpa.domain.UnmappedTrace;
import org.cloud.service.UnmappedTraceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ad/component-mapping/unmapped")
@Api(value = "architecture-discovery-api-v1", description = "Architecture Discovery Api v1endpoints")
public class UnmappedTraceController {

    @Autowired
    private UnmappedTraceService unmappedTraceService;

    @RequestMapping(method = RequestMethod.GET)
    public List<UnmappedTrace> getUnmappedTraces() throws JsonProcessingException {
        return unmappedTraceService.findAll();
    }
}
