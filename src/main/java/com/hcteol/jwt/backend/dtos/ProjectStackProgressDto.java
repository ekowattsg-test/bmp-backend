package com.hcteol.jwt.backend.dtos;

import lombok.Data;

@Data
public class ProjectStackProgressDto {

    private Long projectStackId;
    private String stackName;
    private Long stackNumber;
    private String stackDescription;
    private String status;
}
