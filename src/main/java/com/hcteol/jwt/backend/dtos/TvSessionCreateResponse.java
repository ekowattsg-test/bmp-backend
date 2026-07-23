package com.hcteol.jwt.backend.dtos;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TvSessionCreateResponse {

    private String sessionCode;
    private String pin;
    private String qrPayload;
    private OffsetDateTime challengeExpiresAt;
    private long pollIntervalSeconds;
}
