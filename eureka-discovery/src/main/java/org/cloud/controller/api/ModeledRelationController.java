package org.cloud.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import org.cloud.controller.api.model.ModeledRelationPair;
import org.cloud.jpa.domain.ModeledRelation;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.annotation.Annotation;
import org.cloud.service.ModeledRelationService;
import org.cloud.service.RevisionService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ad/modeled-relation")
@Api(value = "architecture-discovery-api-v1", description = "Architecture Discovery Api v1endpoints")
public class ModeledRelationController {

    @Autowired
    private ModeledRelationService modeledRelationService;

    @Autowired
    private RevisionService revisionService;


    @RequestMapping(method = RequestMethod.POST)
    public ModeledRelation createRelation(@RequestBody ModeledRelationPair relationPair) throws JsonProcessingException, NotFoundException {
        Revision caller = revisionService.findById(relationPair.getCallerId());
        Revision callee = revisionService.findById(relationPair.getCalleeId());

        if (caller == null)
            throw new NotFoundException("Caller not found");
        if (callee == null)
            throw new NotFoundException("Callee not found");

        ModeledRelation existingRelation = modeledRelationService.findByCallerAndCallee(caller, callee);
        if (existingRelation != null)
            modeledRelationService.closeModeledRelation(existingRelation);

        ModeledRelation relation = new ModeledRelation();
        relation.setCaller(caller);
        relation.setCallee(callee);
        return modeledRelationService.saveModeledRelation(relation);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ModeledRelation getModeledRelation(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        ModeledRelation relation = modeledRelationService.findById(id);
        if (relation == null)
            throw new NotFoundException("ModeledRelation not found");
        return relation;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void closeModeledRelation(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        ModeledRelation relation = getModeledRelation(id);
        if (relation.getValidTo() != null)
            throw new IllegalArgumentException("The relation is already closed!");

        modeledRelationService.closeModeledRelation(relation);
    }

    @RequestMapping(value = "/{id}/annotation", method = RequestMethod.POST)
    public ModeledRelation createAnnotation(@PathVariable Long id, @RequestBody Annotation annotation) throws JsonProcessingException, NotFoundException {
        ModeledRelation relation = getModeledRelation(id);
        relation.setAnnotation(annotation.getName(), annotation.getValue());
        return modeledRelationService.saveModeledRelation(relation);
    }

    @RequestMapping(value = "/{id}/annotations", method = RequestMethod.POST)
    public ModeledRelation createAnnotations(@PathVariable Long id, @RequestBody List<Annotation> annotations) throws JsonProcessingException, NotFoundException {
        ModeledRelation relation = getModeledRelation(id);
        for (Annotation annotation : annotations)
            relation.setAnnotation(annotation.getName(), annotation.getValue());
        return modeledRelationService.saveModeledRelation(relation);
    }
}
