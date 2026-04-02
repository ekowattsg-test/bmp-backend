package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProductHierarchy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hierarchyId;
    private Long parentProductId;
    private Long childProductId;
    private Integer numberOfChildren;
}