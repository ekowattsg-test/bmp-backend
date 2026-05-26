package com.hcteol.jwt.backend.entities;

import java.sql.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Staff {

    @Id
    private String staffId;
    @Column(unique = true)
    private String mobileNumber;
    private String staffName;
    private String staffRoleCode;
    private Date serviceStartDate;
    private Date serviceEndDate;
    private String department;
    @Builder.Default
    @Column(nullable = false)
    @ColumnDefault("'S'")
    private String staffType = "S"; // "S" = Internal Staff, "E" = external contractors' staff
    private String location;
    private String staffNumber;
    private String companyId;
    @ColumnDefault("1")
    private Integer active;
}
