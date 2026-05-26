package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OperationStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operationRoleId;
    private String staffId;
    private String roleName;
}
