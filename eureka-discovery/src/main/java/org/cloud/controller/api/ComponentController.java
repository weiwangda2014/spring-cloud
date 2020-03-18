package org.cloud.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import org.cloud.jpa.domain.Revision;
import org.cloud.jpa.domain.annotation.Annotation;
import org.cloud.jpa.domain.component.Activity;
import org.cloud.jpa.domain.component.Component;
import org.cloud.jpa.domain.component.Process;
import org.cloud.service.RevisionService;
import org.cloud.service.component.ActivityService;
import org.cloud.service.component.ComponentService;
import org.cloud.service.component.ProcessService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ad/component")
@Api(value = "architecture-discovery-api-v1", description = "Architecture Discovery Api v1endpoints")
public class ComponentController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    RevisionService revisionService;

    @Autowired
    ProcessService processService;

    @Autowired
    private ComponentService componentService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Component> getComponents() throws JsonProcessingException {
        return componentService.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Component getComponent(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        Component component = componentService.findById(id);
        if (component == null)
            throw new NotFoundException("Component not found");
        return component;
    }

    @RequestMapping(value = "/{id}/annotation", method = RequestMethod.POST)
    public Component createAnnotation(@PathVariable Long id, @RequestBody Annotation annotation) throws Exception {
        Component component = getComponent(id);
        component.setAnnotation(annotation.getName(), annotation.getValue());
        return componentService.saveComponent(component);
    }

    @RequestMapping(value = "/{id}/annotations", method = RequestMethod.POST)
    public Component createAnnotations(@PathVariable Long id, @RequestBody List<Annotation> annotations) throws Exception {
        Component component = getComponent(id);
        for (Annotation annotation : annotations)
            component.setAnnotation(annotation.getName(), annotation.getValue());
        return componentService.saveComponent(component);
    }

    @RequestMapping(value = "/activity", method = RequestMethod.POST)
    public Revision createActivity() throws JsonProcessingException {
        return revisionService.getCurrentRevisionsByComponentId().get(activityService.createActivity().getId());
    }

    @RequestMapping(value = "/activity/{id}/revision", method = RequestMethod.POST)
    public Revision createActivityRevision(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        Activity activity = activityService.findById(id);
        if (activity == null)
            throw new IllegalArgumentException("Activity not found");

        Revision revision = revisionService.getCurrentRevisionsByComponentId().get(activity.getId());
        if (revision.getValidTo() == null)
            revisionService.closeRevision(revision);
        return revisionService.createRevision(activity);
    }

    @RequestMapping(value = "/activity/{id}/revision", method = RequestMethod.DELETE)
    public Revision closeActivityRevision(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        Activity activity = activityService.findById(id);
        if (activity == null)
            throw new IllegalArgumentException("Activity not found");

        Revision revision = revisionService.getCurrentRevisionsByComponentId().get(activity.getId());
        if (revision == null)
            throw new IllegalArgumentException("No current revision found");

        if (revision.getValidTo() == null)
            revision = revisionService.closeRevision(revision);

        return revision;
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public Revision createProcess() throws JsonProcessingException {
        return revisionService.getCurrentRevisionsByComponentId().get(processService.createProcess().getId());
    }

    @RequestMapping(value = "/process/{id}/revision", method = RequestMethod.POST)
    public Revision createProcessRevision(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        Process process = processService.findById(id);
        if (process == null)
            throw new NotFoundException("Process not found");

        Revision revision = revisionService.getCurrentRevisionsByComponentId().get(process.getId());
        if (revision.getValidTo() == null)
            revisionService.closeRevision(revision);
        return revisionService.createRevision(process);
    }

    @RequestMapping(value = "/process/{id}/revision", method = RequestMethod.DELETE)
    public Revision closeProcessRevision(@PathVariable Long id) throws JsonProcessingException, NotFoundException {
        Process process = processService.findById(id);
        if (process == null)
            throw new NotFoundException("Process not found");

        Revision revision = revisionService.getCurrentRevisionsByComponentId().get(process.getId());
        if (revision == null)
            throw new IllegalArgumentException("No current revision found");

        if (revision.getValidTo() == null)
            revision = revisionService.closeRevision(revision);
        return revision;
    }
}
