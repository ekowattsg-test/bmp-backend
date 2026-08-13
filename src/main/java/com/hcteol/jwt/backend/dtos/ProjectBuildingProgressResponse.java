package com.hcteol.jwt.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ProjectBuildingProgressResponse {

    private String projectCode;
    private List<ProjectBlockProgressDto> blocks;
}
