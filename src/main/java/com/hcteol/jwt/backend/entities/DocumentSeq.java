package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class DocumentSeq {

    @Id
    private String docType;
    private Long seq;
    private String token;
     

}
