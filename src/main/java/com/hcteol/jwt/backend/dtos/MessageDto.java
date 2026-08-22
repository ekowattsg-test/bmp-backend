package com.hcteol.jwt.backend.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MessageDto {

    private Long messageId;
    private String senderStaffId;
    private String senderName;
    private String recipientType;
    private String recipientStaffId;
    private String recipientName;
    private String projectCode;
    private String content;
    private String source;
    private String category;
    private String referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private boolean readByMe;
    private String projectGroupScope;
}
