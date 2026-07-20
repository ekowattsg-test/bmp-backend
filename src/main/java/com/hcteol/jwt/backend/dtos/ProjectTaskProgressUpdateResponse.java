package com.hcteol.jwt.backend.dtos;

import com.hcteol.jwt.backend.entities.ProjectTask;
import com.hcteol.jwt.backend.entities.ProjectTaskProgress;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectTaskProgressUpdateResponse {

    private ProjectTaskProgress projectTaskProgress;
    private ProjectTask projectTask;
}
