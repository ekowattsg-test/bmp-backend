package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BriefingMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long briefingMemberId;
    private Long briefingSessionId;
    private String staffId; // staffId of the member, to be used to retrieve the member's name and email from staff table
    private Long currentSeq; // the current slide's sequence number, to be used to retrieve the current slide's content from briefing_content table
    private Integer completed; // 1 if completed briefing saession
}
