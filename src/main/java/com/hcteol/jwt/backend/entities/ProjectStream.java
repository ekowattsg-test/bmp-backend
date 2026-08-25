package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Data
@Entity
@Table(name = "project_stream", uniqueConstraints = {
    @UniqueConstraint(name = "uk_project_stream_project_code_stream_number", columnNames = {"projectCode", "streamNumber"})
})
public class ProjectStream {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectStreamId;
    private String projectCode;
    private String streamType; // "P" - Project Stream, "S" - Sub Stream
    private Long streamNumber;
    private String streamName;
    private String streamDescription;
    private String streamStartDate;
    private String streamEndDate;
    private Long parentStreamNumber;
}
