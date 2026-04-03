package com.hcteol.jwt.backend.entities;

import java.util.List;

import com.hcteol.jwt.backend.config.BundleMemberListJsonConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class ProductBundle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bundleId;
    private String bundleCode;
    private String bundleName;
    @Convert(converter = BundleMemberListJsonConverter.class)
    @Column(columnDefinition = "JSON")
    private List<BundleMember> bundleMembers;
}