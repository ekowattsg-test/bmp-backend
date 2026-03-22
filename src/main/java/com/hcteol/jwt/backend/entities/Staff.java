package com.hcteol.jwt.backend.entities;

import java.sql.Date;

import org.hibernate.annotations.ColumnDefault;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private String mobileNumber;
    private String staffName;
    private String staffId;
    private String staffRoleCode;
    private Date serviceStartDate;
    private Date serviceEndDate;
    private String department;
    private String staffNumber;
    private String companyId;
    @ColumnDefault("1")
    private Integer active;
}
