package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class LibraryCatelog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long libraryCatelogId;
    private String libraryCatelogName;
    private Integer active; // defaultl 0; only one of all library catelog can have active = 1
    private Integer visibleLevel; // 0:viible to all, > 0: visible by user level
    private String description;
    private String projectCode; // the project code that this library catelog belongs to, to be used to retrieve the project name from project table
    @Column(columnDefinition = "TEXT")
    private String quicSearchKey;

}
