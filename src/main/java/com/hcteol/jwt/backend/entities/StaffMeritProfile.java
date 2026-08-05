package com.hcteol.jwt.backend.entities;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class StaffMeritProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffMeritProfileId;
    private String staffId;
    private Long staffMeritId;
    private String issuedBy;
    private Date issuedDate;
    private Integer meritPoints; // -10 to 10Positive for Merit, Negative for Demerit
    private String meritRemarks;
    @Column(length = 2000)
    private String documentationLink;
}
