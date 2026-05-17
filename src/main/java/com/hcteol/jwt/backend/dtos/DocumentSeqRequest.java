package com.hcteol.jwt.backend.dtos;

import lombok.Data;

@Data
public class DocumentSeqRequest {

    private String docType;
    private String token;
}
