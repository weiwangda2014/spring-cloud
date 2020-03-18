package org.cloud.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import org.cloud.controller.api.model.Architecture;
import org.cloud.controller.api.model.RelationFilter;
import org.cloud.jpa.domain.Revision;
import org.cloud.service.RevisionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "http://localhost:9000")
@RequestMapping("/api/v1/ad/architecture")
@Api(value = "architecture-discovery-api-v1", description = "Architecture Discovery Api v1endpoints")
public class ArchitectureController {

    @Autowired
    private RevisionService revisionService;

    @RequestMapping(method = RequestMethod.GET)
    public Architecture getArchitecture(@RequestParam(value = "snapshot", required = false) Long snapshot,
                                        @RequestParam(value = "annotation-filter", required = false, defaultValue = "") String[] annotationFilter,
                                        @RequestParam(value = "component-type-filter", required = false, defaultValue = "") String[] componentTypeFilter,
                                        @RequestParam(value = "relation-filter", defaultValue = "ALL", required = false) RelationFilter relationFilter) throws JsonProcessingException {
        Architecture architecture = new Architecture();
        architecture.setRelationFilter(relationFilter);
        architecture.setAnnotationFilters(Arrays.asList(annotationFilter));

        List<Revision> revisions;
        if (snapshot == null) {
            architecture.setSnapshot(null);
            revisions = new ArrayList<>(revisionService.getCurrentRevisionsByComponentId().values());
        } else {
            architecture.setSnapshot(new Date(snapshot));
            revisions = revisionService.getRevisionsByDate(architecture.getSnapshot());
        }

        if (componentTypeFilter.length > 0) {
            for (int i = 0; i < componentTypeFilter.length; ++i)
                componentTypeFilter[i] = componentTypeFilter[i].toLowerCase();
            List<String> typeFilters = Arrays.asList(componentTypeFilter);
            revisions = revisions.stream().filter(r -> typeFilters.contains(r.getComponent().getClass().getSimpleName().toLowerCase())).collect(Collectors.toList());
        }

        architecture.setRevisions(revisions);
        return architecture;
    }
}
