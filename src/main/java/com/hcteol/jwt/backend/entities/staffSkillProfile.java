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
public class staffSkillProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffSkillProfileId;
    private String staffName;
    private Long staffSkillId;
    private String issuedBy;
    private Date acquiredDate;
    private Date expiryDate;
    private Integer noExpiry;
    @Column(length = 2000)
    private String certificationLink;
}