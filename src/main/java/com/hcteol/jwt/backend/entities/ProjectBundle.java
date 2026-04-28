package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProjectBundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectBundleId;
    private Long projectTaskId;
    private Long bundleId;
    private Long quantity;
}