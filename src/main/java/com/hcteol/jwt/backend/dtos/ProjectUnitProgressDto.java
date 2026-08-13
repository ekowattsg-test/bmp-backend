package com.hcteol.jwt.backend.dtos;

import java.util.List;

import lombok.Data;

@Data
public class ProjectUnitProgressDto {

    private Long projectUnitId;
    private String unitName;
    private Long unitNumber;
    private String unitDescription;
    private Long projectStackId;
    private String stackName;
    private Long projectStreamId;
    private String streamName;
    private Integer progress;
    private String plannedStartDate;
    private String plannedEndDate;
    private String actualStartDate;
    private String actualEndDate;
    private String status;
    private List<ProjectUnitWorkProgressDto> works;
}
