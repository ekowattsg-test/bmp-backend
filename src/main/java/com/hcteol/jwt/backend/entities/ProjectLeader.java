package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectLeader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectLeaderId;
    private String projectCode;
    private String projectLeaderStaffId;
    private String projectRole; // e.g., "M" Manager, "L" Leader, "C" Co-leader. each project can have only one Manager and one Leader
    private String roleStartDate;
    private String roleEndDate; // null for current role
    private Integer active; // 1 for active, 0 for inactive
}
