package com.hcteol.jwt.backend.entities;

import java.sql.Date;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@Immutable
@Subselect("select row_number() over (order by v.staff_name, v.skill_name, v.staff_skill_profile_id) as row_id, v.* from staff_skill_profile_view v")
@Synchronize({"staff_skill_profile", "staff", "staff_skill"})
public class StaffSkillProfileView {

    @Id
    @Column(name = "row_id")
    private Long rowId;

    @Column(name = "staff_skill_profile_id")
    private Long staffSkillProfileId;

    @Column(name = "acquired_date")
    private Date acquiredDate;

    @Column(name = "certification_link")
    private String certificationLink;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "issued_by")
    private String issuedBy;

    @Column(name = "no_expiry")
    private Integer noExpiry;

    @Column(name = "staff_skill_id")
    private Long staffSkillId;

    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "staff_name")
    private String staffName;

    @Column(name = "skill_category")
    private String skillCategory;

    @Column(name = "skill_description")
    private String skillDescription;

    @Column(name = "skill_name")
    private String skillName;
}
