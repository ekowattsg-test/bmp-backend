package com.hcteol.jwt.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ProjectBlockProgressDto {

    private Long projectBlockId;
    private String blockName;
    private Long blockNumber;
    private String blockDescription;
    private String status;
    private List<ProjectStoreyProgressDto> storeys;
    private List<ProjectStackProgressDto> stacks;
}
