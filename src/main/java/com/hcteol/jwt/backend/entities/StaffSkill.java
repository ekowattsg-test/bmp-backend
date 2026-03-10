package com.hcteol.jwt.backend.entities;

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
public class StaffSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffSkillId;
    private String skillName;
    private String skillDescription;
    private String skillCategory;

}