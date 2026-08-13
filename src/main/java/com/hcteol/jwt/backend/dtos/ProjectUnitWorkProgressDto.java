package com.hcteol.jwt.backend.dtos;

import lombok.Data;

@Data
public class ProjectUnitWorkProgressDto {

    private Long projectUnitWorkId;
    private Long projectTaskId;
    private String workName;
    private Integer progress;
    private String plannedStartDate;
    private String plannedEndDate;
    private String actualStartDate;
    private String actualEndDate;
    private String status;
}