package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectTaskType {

    @Id
    private String projectTaskCode;
    private String projectTaskDescription;
    private Integer userTask; // will this task show task type dropdown
    private Integer editStartDate; // allow user to edit start date
    private Integer createByStream; // 1 - create by stream only, 0 - create by task only
    private Integer canDelete;
    private Long minimumDays;
    private Long maximumDays;
    private String alignWith;
    private String inventoryType; // "any", "stock", "asset", "none"
    private Integer manpowerRequired; // 1 - manpower required, 0 - no manpower required

}
