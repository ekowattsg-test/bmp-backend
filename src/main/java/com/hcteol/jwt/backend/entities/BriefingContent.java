package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class BriefingContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long briefingContentId;
    private Long briefingId;
    private String sequenceNumber;
    @Column(columnDefinition = "TEXT")
    private String imageKey;
    private String contentTitle;
    private String contentText; // base of brif=efing content, to be written in base language defined in param
    @Column(columnDefinition = "TEXT")
    private String translatedText; // contentText translated into languages supported by system, stored in Json
}
