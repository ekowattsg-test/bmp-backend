package com.hcteol.jwt.backend.dtos;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TvSessionStatusResponse {

    private String sessionCode;
    private String status;
    private String exchangeCode;
    private String destinationUrl;
    private OffsetDateTime challengeExpiresAt;
    private OffsetDateTime sessionExpiresAt;
}
