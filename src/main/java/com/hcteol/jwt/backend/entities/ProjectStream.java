package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ProjectStream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectStreamId;
    private String projectCode;
    private String streamType; // "P" - Project Stream, "S" - Sub Stream
    private Long streamNumber;
    private String streamName;
    private String streamDescription;
    private Date streamStartDate;
    private Date streamEndDate;
}
