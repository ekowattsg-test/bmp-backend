package com.hcteol.jwt.backend.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "userrole_view")
public class UserRoleView {
    @Id
    private Long userrole_id;
    private Long user_id;
    private Long level;
    private Long role_id;
    private String Role;
    private String company_id;

}
