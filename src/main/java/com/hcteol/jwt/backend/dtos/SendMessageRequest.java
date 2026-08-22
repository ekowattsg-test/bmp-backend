package com.hcteol.jwt.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class SendMessageRequest {

    private String recipientType; // DIRECT, PROJECT, BROADCAST
    private String recipientStaffId;
    private String projectCode;
    private String content;
    private String category;
    private String referenceId;
    private String projectGroupScope; // LEADERSHIP or ALL, only for PROJECT type
}
