package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class OperationRole {

    @Id
    private String roleName;
    private String roleDescription;
}
