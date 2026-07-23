package com.hcteol.jwt.backend.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "tv_screen_session")
public class TvScreenSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tvScreenSessionId;

    @Column(nullable = false, unique = true, length = 64)
    private String sessionCode;

    @Column(length = 8)
    private String pin;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime challengeExpiresAt;

    private LocalDateTime approvedAt;

    private LocalDateTime exchangedAt;

    @Column(nullable = false)
    private LocalDateTime sessionExpiresAt;

    @Column(unique = true, length = 64)
    private String exchangeCode;

    private LocalDateTime exchangeExpiresAt;

    private String approvedByLogin;

    private String destinationUrl;
}
