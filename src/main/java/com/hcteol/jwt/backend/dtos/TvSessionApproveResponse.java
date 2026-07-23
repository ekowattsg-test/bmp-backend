package com.hcteol.jwt.backend.dtos;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TvSessionApproveResponse {

    private String sessionCode;
    private String status;
    private OffsetDateTime approvedAt;
    private OffsetDateTime sessionExpiresAt;
}
