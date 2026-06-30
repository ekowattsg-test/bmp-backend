package com.hcteol.jwt.backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.entities.ProjectManpowerView;
import com.hcteol.jwt.backend.services.ProjectManpowerViewService;

@RestController
@RequestMapping("/api/projectmanpowerviews")
public class ProjectManpowerViewController {

    @Autowired
    private ProjectManpowerViewService projectManpowerViewService;

    @GetMapping
    public List<ProjectManpowerView> searchProjectManpowerViews(
            @RequestParam(required = false) String projectCode,
            @RequestParam(required = false) Long projectStreamId,
            @RequestParam(required = false) Long projectTaskId,
            @RequestParam(required = false) String staffId,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) Integer active,
            @RequestParam(required = false) String projectStatus) {
        return projectManpowerViewService.getProjectManpowerViews(
                projectCode,
                projectStreamId,
                projectTaskId,
                staffId,
                taskStatus,
                active,
                projectStatus);
    }

    @GetMapping("/{rowId}")
    public ResponseEntity<ProjectManpowerView> getProjectManpowerViewById(@PathVariable Long rowId) {
        return projectManpowerViewService.getProjectManpowerViewById(rowId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/project/{projectCode}")
    public List<ProjectManpowerView> getByProjectCode(@PathVariable String projectCode) {
        return projectManpowerViewService.getProjectManpowerViewsByProjectCode(projectCode);
    }

    @GetMapping("/task/{projectTaskId}")
    public List<ProjectManpowerView> getByTaskId(@PathVariable Long projectTaskId) {
        return projectManpowerViewService.getProjectManpowerViewsByTaskId(projectTaskId);
    }

    @GetMapping("/staff/{staffId}")
    public List<ProjectManpowerView> getByStaffId(@PathVariable String staffId) {
        return projectManpowerViewService.getProjectManpowerViewsByStaffId(staffId);
    }
}
