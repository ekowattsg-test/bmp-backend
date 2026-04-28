package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectStream {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectStreamId;
    private String projectCode;
    private Long streamNumber;
    private String streamName;
    private String streamDescription;
    private String mobileNumber; // person in-charge of stream
    private Boolean active;
}