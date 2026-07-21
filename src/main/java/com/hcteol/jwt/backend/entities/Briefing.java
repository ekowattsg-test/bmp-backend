package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Briefing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long briefingId;
    private String briefingTitle;
    private String briefingDescription;
    private Integer active; // default 0; active marker is managed per briefing package
}
