package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Project {
    @Id
    private String projectCode;
    private String projectName;
    private String projectDescription;
    private Long customerId;
    private String startDate;
    private String endDate;
    private String projectLocation;
    private String mobileNumber; // person in-charge of project
    private Boolean active;
    private Long streamCount;
}