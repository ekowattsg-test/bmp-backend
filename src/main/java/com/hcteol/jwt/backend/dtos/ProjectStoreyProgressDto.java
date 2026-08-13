package com.hcteol.jwt.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ProjectStoreyProgressDto {

    private Long projectStoreyId;
    private String storeyName;
    private Long storeyNumber;
    private String storeyDescription;
    private String status;
    private List<ProjectUnitProgressDto> units;
}
