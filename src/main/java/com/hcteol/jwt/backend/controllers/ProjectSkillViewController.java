package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.ProjectSkillView;
import com.hcteol.jwt.backend.services.ProjectSkillViewService;

@RestController
@RequestMapping("/api/projectskillviews")
public class ProjectSkillViewController {

    @Autowired
    private ProjectSkillViewService projectSkillViewService;

    @GetMapping
    public List<ProjectSkillView> searchProjectSkillViews(
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) Long projectStreamId,
            @RequestParam(required = false) Long projectTaskId,
            @RequestParam(required = false) Long staffSkillId,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) Integer active,
            @RequestParam(required = false) String projectStatus) {
        return projectSkillViewService.getProjectSkillViews(
                projectCode,
                projectStreamId,
                projectTaskId,
                staffSkillId,
                taskStatus,
                active,
                projectStatus);
    }

    @GetMapping("/{rowId}")
    public ResponseEntity<ProjectSkillView> getProjectSkillViewById(@PathVariable Long rowId) {
        return projectSkillViewService.getProjectSkillViewById(rowId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectCode}")
    public List<ProjectSkillView> getByProjectCode(@PathVariable String projectCode) {
        return projectSkillViewService.getProjectSkillViewsByProjectCode(projectCode);
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectSkillView> getByTaskId(@PathVariable Long projectTaskId) {
        return projectSkillViewService.getProjectSkillViewsByTaskId(projectTaskId);
    }

    @GetMapping("/skill/{staffSkillId}")
    public List<ProjectSkillView> getByStaffSkillId(@PathVariable Long staffSkillId) {
        return projectSkillViewService.getProjectSkillViewsByStaffSkillId(staffSkillId);
    }
}
