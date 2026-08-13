package com.hcteol.jwt.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcteol.jwt.backend.dtos.ProjectBuildingProgressResponse;
import com.hcteol.jwt.backend.services.ProjectBuildingProgressService;

@RestController
@RequestMapping("/api/projectbuildingprogress")
public class ProjectBuildingProgressController {

    @Autowired
    private ProjectBuildingProgressService projectBuildingProgressService;

    @GetMapping("/{projectCode}")
    public ProjectBuildingProgressResponse getProjectBuildingProgress(@PathVariable String projectCode) {
        return projectBuildingProgressService.computeProgress(projectCode);
    }
}
