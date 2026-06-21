package com.hcteol.jwt.backend.entities;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class ProductBundle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bundleId;
    private String bundleCode;
    private String bundleName;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bundle_id")
    private List<BundleMember> bundleMembers;
}
