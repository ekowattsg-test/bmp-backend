package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BriefingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long briefingSessionId;
    private String projectCode;
    private String briefingDate;
    private Long briefingId;
    private String presenter; // staffId of the presenter, to be used to retrieve the presenter's name and email from staff table
    private String startTime;
    private Long currentSeq; // the current slide's sequence number, to be used to retrieve the current slide's content from briefing_content table
    private String endTime;
}
