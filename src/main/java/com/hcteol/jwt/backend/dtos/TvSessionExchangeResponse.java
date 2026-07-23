package com.hcteol.jwt.backend.dtos;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TvSessionExchangeResponse {

    private String token;
    private String destinationUrl;
    private OffsetDateTime sessionExpiresAt;
    private long refreshIntervalSeconds;
    private List<String> projectCodes;
}
