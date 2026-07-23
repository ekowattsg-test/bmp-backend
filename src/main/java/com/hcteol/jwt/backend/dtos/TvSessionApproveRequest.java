package com.hcteol.jwt.backend.dtos;

import lombok.Data;

@Data
public class TvSessionApproveRequest {

    private String sessionCode;
    private String pin;
    private String destinationUrl;
}
