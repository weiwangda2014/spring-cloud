package org.cloud.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wordnik.swagger.annotations.Api;
import org.cloud.jpa.domain.Changelog;
import org.cloud.service.ChangelogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ad/changelog")
@Api(value = "architecture-discovery-api-v1", description = "Architecture Discovery Api v1endpoints")
public class ChangelogController {

    @Autowired
    private ChangelogService changelogService;

    @RequestMapping(method = RequestMethod.GET)
    public List<Changelog> getChangelogs(@RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) throws JsonProcessingException {
        limit = Integer.min(limit, 200);
        List<Changelog> changelogs = changelogService.findAll();
        return (changelogs.size() <= limit) ? changelogs : changelogs.subList(Math.max(changelogs.size() - limit, 0), changelogs.size());
    }
}
