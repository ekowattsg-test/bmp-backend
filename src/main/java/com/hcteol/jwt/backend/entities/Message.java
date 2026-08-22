package com.hcteol.jwt.backend.entities;

import java.time.LocalDateTime;

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
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @Column(nullable = false)
    private String senderStaffId;

    @Column(nullable = false, length = 20)
    private String recipientType; // DIRECT, PROJECT, BROADCAST

    private String recipientStaffId;

    private String projectCode;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, length = 20)
    private String source; // USER, SYSTEM

    @Column(length = 50)
    private String category; // e.g. DO_CREATED, PO_RECEIVED, GENERAL

    @Column(length = 100)
    private String referenceId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @Column(length = 20)
    private String projectGroupScope; // LEADERSHIP, ALL (only for PROJECT type)
}
