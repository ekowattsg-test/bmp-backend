package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectStreamAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectStreamAssetId;
    private Long projectStreamId;
    private Long productId;
    private Double quantity;
}
